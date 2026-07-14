package ac.comet.cometac.manager;

import ac.comet.cometac.CometAPI;
import ac.grim.grimac.api.GrimUser;
import ac.grim.grimac.api.config.ConfigManager;
import ac.comet.cometac.manager.init.ReloadableInitable;
import ac.comet.cometac.manager.init.start.StartableInitable;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.LogUtil;
import ac.comet.cometac.utils.anticheat.MessageUtil;
import ac.comet.cometac.utils.common.arguments.CommonGrimArguments;
import ac.comet.cometac.utils.data.Pair;
import ac.comet.cometac.utils.data.webhook.discord.CompiledDiscordTemplate;
import ac.comet.cometac.utils.data.webhook.discord.Embed;
import ac.comet.cometac.utils.data.webhook.discord.EmbedAuthor;
import ac.comet.cometac.utils.data.webhook.discord.EmbedField;
import ac.comet.cometac.utils.data.webhook.discord.EmbedFooter;
import ac.comet.cometac.utils.data.webhook.discord.WebhookMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class DiscordManager implements StartableInitable, ReloadableInitable {
    private static final Predicate<String> WEBHOOK_REGEX = Pattern.compile("^https://(?:canary\\.)?discord\\.com/api(?:/v\\d+)?/webhooks/\\d+/[\\w-]+(\\?thread_id=\\d+)?$").asMatchPredicate();
    private static final Predicate<String> HTTPS_URL_REGEX = Pattern.compile("^https://[^/\\s]+/\\S+$").asMatchPredicate();
    private static final Duration timeout = Duration.ofMillis(CommonGrimArguments.URL_TIMEOUT.value());
    private static final HttpClient client = HttpClient.newBuilder().connectTimeout(timeout).build();
    private static final ConcurrentLinkedDeque<Pair<HttpRequest, CompletableFuture<Boolean>>> requests = new ConcurrentLinkedDeque<>();
    private static final AtomicBoolean taskStarted = new AtomicBoolean();
    private static final AtomicBoolean sending = new AtomicBoolean();
    private static long rateLimitedUntil;
    private URI url;
    private int embedColor;
    private CompiledDiscordTemplate compiledContent;
    private char backtickReplacement = '\u02CB';
    private String embedTitle = "";
    private boolean includeTimestamp;
    private boolean includeVerbose;
    private @Nullable String embedImageUrl;
    private @Nullable String embedThumbnailUrl;
    private @Nullable String embedFooterUrl;
    private String embedFooterText = "";
    private String embedAuthorText = "";
    // Grouped-field templates, compiled once per reload and rendered per alert.
    private CompiledDiscordTemplate playerFieldTemplate;
    private CompiledDiscordTemplate checkFieldTemplate;
    private CompiledDiscordTemplate serverFieldTemplate;
    // Bundled comet logo, attached to each alert and referenced from the embed as attachment://<name>.
    private static final String LOGO_NAME = "cometac-logo.png";
    private static final byte @Nullable [] LOGO_BYTES = loadLogo();
    private boolean useLogo;

    private static byte @Nullable [] loadLogo() {
        try (java.io.InputStream in = DiscordManager.class.getResourceAsStream("/" + LOGO_NAME)) {
            return in == null ? null : in.readAllBytes();
        } catch (Exception e) {
            return null;
        }
    }

    private static final Pattern URL_PATTERN = Pattern.compile("^https?://(?:www\\.)?[-a-z0-9@:%._+~#=]{1,256}\\.[a-z0-9()]{1,6}\\b[-a-z0-9()@:%_+.~#?&/=]*$", Pattern.CASE_INSENSITIVE);

    private static String validatedConfigURL(String configPath, String defaultURL) {
        String url = CometAPI.INSTANCE.getConfigManager().getConfig().getStringElse("embed-image-url", defaultURL);
        if (url == null || url.isBlank()) return null;
        if (URL_PATTERN.matcher(url).matches()) {
            return url;
        } else {
            LogUtil.warn("Invalid embed url for config path " + configPath + ": " + configPath);
            return defaultURL;
        }
    }

    @Override
    public void start() {
        reload();
    }

    @Override
    public void reload() {
        try {
            // Yes all of these fields should technically be volatile so they will be updated correctly on reload for HTTP threads to read
            // No we're not going to pay for atomic reads in the hot loop however cheap for a one in a billion chance to read an outdated config
            // When your discord webhook settings are changed (who changes them in prod?) that can be fixed with a restart
            ConfigManager config = CometAPI.INSTANCE.getConfigManager().getConfig();
            if (!config.getBooleanElse("enabled", false)) {
                url = null;
                return;
            }

            String webhook = config.getStringElse("webhook", "");
            boolean strictValidation = !config.getBooleanElse("disable-webhook-validation", false);

            if (webhook.isEmpty()) {
                url = null;
            } else if (strictValidation) {
                if (!WEBHOOK_REGEX.test(webhook)) {
                    LogUtil.error("Discord webhook URL does not match expected format"
                            + " (https://discord.com/api/webhooks/<id>/<token>): " + webhook);
                    LogUtil.error("If you are using a proxy or custom endpoint,"
                            + " set 'disable-webhook-validation: true' in the Discord config.");
                    url = null;
                } else {
                    url = new URI(webhook);
                }
            } else {
                if (!HTTPS_URL_REGEX.test(webhook)) {
                    LogUtil.error("Discord webhook URL is not a valid HTTPS URL: " + webhook);
                    url = null;
                } else {
                    LogUtil.info("Webhook validation disabled — using custom endpoint: "
                            + webhook.substring(0, Math.min(webhook.length(), 40)) + "...");
                    url = new URI(webhook);
                }
            }
            // not adding these to the config since they may change in the future
            // mainly for just for allowing more customization
            embedImageUrl = validatedConfigURL("embed-image-url", null);
            embedThumbnailUrl = validatedConfigURL("embed-thumbnail-url", "https://crafthead.net/helm/%uuid%");
            // When the bundled logo is used it overrides the footer icon via attachment://; the URL stays a fallback.
            useLogo = config.getBooleanElse("embed-use-logo", true) && LOGO_BYTES != null;
            embedFooterUrl = validatedConfigURL("embed-footer-url", "https://cometac.ac/images/cometac.png");
            embedFooterText = config.getStringElse("embed-footer-text", "CometAC Protection");
            embedAuthorText = config.getStringElse("embed-author", "CometAC Alert");
            embedTitle = config.getStringElse("embed-title", "");

            try {
                embedColor = Color.decode(config.getStringElse("embed-color", "#1E90FF")).getRGB();
            } catch (NumberFormatException e) {
                LogUtil.warn("Discord embed color is invalid");
            }

            StringBuilder sb = new StringBuilder();
            for (String string : config.getStringListElse("violation-content", getDefaultContents())) {
                sb.append(string).append("\n");
            }
            includeTimestamp = config.getBooleanElse("include-timestamp", true);
            includeVerbose = config.getBooleanElse("include-verbose", true);
            String btReplace = config.getStringElse("backtick-replacement-char", "\u02CB");
            backtickReplacement = (btReplace.isEmpty()) ? '\u02CB' : btReplace.charAt(0);
            compiledContent = CompiledDiscordTemplate.compile(sb.toString());
            // Grouped detail blocks shown as three inline fields, mirroring a clean staff-alert layout.
            playerFieldTemplate = CompiledDiscordTemplate.compile(String.join("\n",
                    config.getStringListElse("embed-field-player", List.of("Name: `%player%`", "Ping: `%ping% ms`"))));
            checkFieldTemplate = CompiledDiscordTemplate.compile(String.join("\n",
                    config.getStringListElse("embed-field-check", List.of("Check: `%check%`", "Violations: `%violations%`"))));
            serverFieldTemplate = CompiledDiscordTemplate.compile(String.join("\n",
                    config.getStringListElse("embed-field-server", List.of("TPS: `%tps%`", "Brand: `%brand%`", "Client: `%version%`"))));
        } catch (Exception e) {
            LogUtil.error("Failed to load Discord webhook configuration", e);
        }
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull @Unmodifiable List<@NotNull String> getDefaultContents() {
        return List.of("**%player%** failed **%check%**");
    }

    public void sendAlert(@NotNull CometPlayer player, String verbose, String checkName, int violations) {
        if (isDisabled()) {
            return;
        }

        // Per-alert overlay — avoids polluting the global static map
        Map<String, String> statics = new HashMap<>(CometAPI.INSTANCE.getExternalAPI().getStaticReplacements());
        statics.put("%check%", checkName);
        statics.put("%violations%", Integer.toString(violations));

        Map<String, Function<GrimUser, String>> dynamics = CometAPI.INSTANCE.getExternalAPI().getVariableReplacements();

        String content = compiledContent.render(player, statics, dynamics, backtickReplacement);
        String logoRef = useLogo ? "attachment://" + LOGO_NAME : embedFooterUrl;

        // Build every field up front so addFields() is only ever called once.
        java.util.List<EmbedField> fields = new java.util.ArrayList<>(4);
        fields.add(new EmbedField("Player", playerFieldTemplate.render(player, statics, dynamics, backtickReplacement), true));
        fields.add(new EmbedField("Check", checkFieldTemplate.render(player, statics, dynamics, backtickReplacement), true));
        fields.add(new EmbedField("Server", serverFieldTemplate.render(player, statics, dynamics, backtickReplacement), true));
        if (!verbose.isEmpty() && includeVerbose) {
            // Fenced block so multi-token verbose stays readable; backtick-escape prevents breakout.
            String safeVerbose = CompiledDiscordTemplate.escapeCodeSpan(verbose, backtickReplacement);
            fields.add(new EmbedField("Verbose", "```\n" + safeVerbose + "\n```", false));
        }

        Embed embed = new Embed(content)
                .color(embedColor)
                .author(new EmbedAuthor(
                        MessageUtil.replacePlaceholders(player, embedAuthorText, false),
                        null,
                        logoRef))
                .thumbnailURL(MessageUtil.replacePlaceholders(player, embedThumbnailUrl, false))
                .imageURL(MessageUtil.replacePlaceholders(player, embedImageUrl, false))
                .addFields(fields.toArray(new EmbedField[0]))
                .footer(new EmbedFooter(
                        MessageUtil.replacePlaceholders(player, embedFooterText, false),
                        logoRef
                ));

        if (embedTitle != null && !embedTitle.isEmpty()) {
            embed.title(MessageUtil.replacePlaceholders(player, embedTitle, false));
        }

        if (includeTimestamp) embed.timestamp(Instant.now());

        WebhookMessage message = new WebhookMessage().addEmbeds(embed);
        if (useLogo && LOGO_BYTES != null) message.addAttachment(LOGO_NAME, LOGO_BYTES);
        sendWebhookMessage(message);
    }

    public CompletableFuture<Boolean> sendWebhookMessage(WebhookMessage message) {
        if (isDisabled()) return CompletableFuture.completedFuture(false);

        HttpRequest request = buildRequest(message);

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        requests.add(new Pair<>(request, future));

        if (!taskStarted.getAndSet(true)) {
            // there's probably a better way to handle rate limits, but this works, so whatever.
            CometAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(CometAPI.INSTANCE.getGrimPlugin(), DiscordManager::tick, 0, 1);
        }

        return future;
    }

    public boolean isDisabled() {
        return url == null;
    }

    // Plain JSON POST unless the message carries file attachments, in which case Discord
    // requires multipart/form-data with a payload_json part plus one files[i] part each.
    private HttpRequest buildRequest(WebhookMessage message) {
        String json = message.toJson().toString();
        java.util.List<WebhookMessage.Attachment> atts = message.attachments();
        if (atts == null || atts.isEmpty()) {
            return HttpRequest.newBuilder()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(timeout)
                    .build();
        }

        String boundary = "cometac" + Long.toHexString(System.nanoTime());
        java.io.ByteArrayOutputStream body = new java.io.ByteArrayOutputStream();
        appendAscii(body, "--" + boundary + "\r\n");
        appendAscii(body, "Content-Disposition: form-data; name=\"payload_json\"\r\n");
        appendAscii(body, "Content-Type: application/json\r\n\r\n");
        appendBytes(body, json.getBytes(StandardCharsets.UTF_8));
        appendAscii(body, "\r\n");
        int i = 0;
        for (WebhookMessage.Attachment a : atts) {
            appendAscii(body, "--" + boundary + "\r\n");
            appendAscii(body, "Content-Disposition: form-data; name=\"files[" + i + "]\"; filename=\"" + a.filename() + "\"\r\n");
            appendAscii(body, "Content-Type: application/octet-stream\r\n\r\n");
            appendBytes(body, a.data());
            appendAscii(body, "\r\n");
            i++;
        }
        appendAscii(body, "--" + boundary + "--\r\n");

        return HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .timeout(timeout)
                .build();
    }

    private static void appendAscii(java.io.ByteArrayOutputStream out, String s) {
        appendBytes(out, s.getBytes(StandardCharsets.UTF_8));
    }

    private static void appendBytes(java.io.ByteArrayOutputStream out, byte[] b) {
        out.write(b, 0, b.length);
    }

    private static void tick() {
        Pair<HttpRequest, CompletableFuture<Boolean>> pair = requests.peek();
        if (pair != null && rateLimitedUntil < System.currentTimeMillis() && !sending.getAndSet(true)) {
            HttpRequest request = pair.first();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).whenComplete((response, throwable) -> {
                if (throwable != null) {
                    sending.set(false);
                    LogUtil.error("Exception caught while sending a Discord webhook alert", throwable);
                    return;
                }

                if (response != null && response.statusCode() == 429) {
                    sending.set(false);
                    rateLimitedUntil = Math.max(response.headers().firstValueAsLong("X-RateLimit-Reset").getAsLong() * 1000, rateLimitedUntil);
                    return;
                }

                requests.remove(pair);
                sending.set(false);

                // TODO: handle 503 (Service Unavailable)?
                if (response != null && response.statusCode() >= 400) {
                    LogUtil.error("Encountered status code " + response.statusCode() + " with body " + response.body() + " and headers " + response.headers().map() + " while sending a Discord webhook alert.");
                    pair.second().complete(false);
                } else {
                    pair.second().complete(true);
                }
            });
        }
    }
}
