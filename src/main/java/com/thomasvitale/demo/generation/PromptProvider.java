package com.thomasvitale.demo.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

/**
 * Builds Spring AI {@link Prompt} instances for each story part, variant, and style.
 */
@Component
public class PromptProvider {

    public static final int TOTAL_PARTS = 5;

    // -------------------------------------------------------------------------
    // Domain types
    // -------------------------------------------------------------------------

    public enum StoryModel {
        SMALL,
        LARGE
    }

    public enum StoryTheme {
        MOON(1), CIRCUS(2);

        private final int roundNumber;

        StoryTheme(int roundNumber) {
            this.roundNumber = roundNumber;
        }

        public int roundNumber() {
            return roundNumber;
        }
    }

    public enum StoryStyle {
        FUNNY, DRY
    }

    record Beat(String title, String instructions, String priorContext) {
        Beat(String title, String instructions) {
            this(title, instructions, null);
        }
    }

    // -------------------------------------------------------------------------
    // Style instructions
    // -------------------------------------------------------------------------

    private static final String STYLE_INSTRUCTION_FUNNY = """
            Write in the style of Douglas Adams. STRICT LIMIT on similes (comparisons using \
            "like" or "as"): zero or one per part. Zero is preferred. Never open a paragraph \
            with a simile. One good extended metaphor per part maximum; don't stack metaphors.""";

    private static final String STYLE_INSTRUCTION_DRY = """
            Write as a deadpan narrator — completely serious, treating every absurd situation \
            with total gravitas, but still telling a clear, readable story. Think aviation \
            incident report or nature documentary narration: precise, humorless, matter-of-fact. \
            Sprinkle in technical jargon and elevated vocabulary where it adds comedic contrast \
            (e.g., "gravitational insufficiency," "autonomous hardware migration," "thermal \
            management degradation") — but the story itself must remain followable. The audience \
            should understand the plot while enjoying the absurdly formal language. No jokes, \
            no puns, no wordplay, no exclamation marks. Document Nyx's panicked outbursts by \
            quoting them directly, then continuing in calm, measured narration as if nothing \
            unusual occurred.""";

    private static final String STYLE_INSTRUCTION_CIRCUS = """
            Write in the style of Douglas Adams. STRICT LIMIT on similes (comparisons using \
            "like" or "as"): zero or one per part. Zero is preferred. Never open a paragraph \
            with a simile. One good extended metaphor per part maximum; don't stack metaphors. \
            Rae is a pure keyboard warrior who has never done anything remotely physical.""";

    // -------------------------------------------------------------------------
    // Theme 1 – Moon beats
    // -------------------------------------------------------------------------

    private static final Map<Integer, Map<StoryStyle, Beat>> THEME1_BEATS = Map.of(
        1, Map.of(
            StoryStyle.FUNNY, new Beat(
                "Setup — Mystery Mission on the Moon",
                """
                Nyx Vasquez, platform engineer, arrives on the moon. They don't know why they've \
                been sent — it's a mystery mission. Nobody explained the purpose. They look around \
                at the barren lunar landscape — grey dust, no atmosphere, one-sixth Earth gravity \
                — and do the only thing a platform engineer knows how to do: start setting up a \
                platform. They unpack servers, run cables, configure infrastructure. Lean into \
                the physical absurdity of doing IT work on the moon: no air, bulky spacesuit, \
                everything covered in moon dust. Establish Nyx's confusion about why they're \
                here, but also their compulsive need to build. DO NOT mention gravity problems, \
                floating hardware, or things drifting away — that is the plot of Part 2."""
            ),
            StoryStyle.DRY, new Beat(
                "Initial Deployment — Lunar Surface Provisioning",
                """
                Platform engineer Nyx Vasquez is deployed to the lunar surface. No one told them \
                why. No mission briefing, no requirements document, no Slack message explaining \
                the purpose. Nyx surveys the environment: grey dust, vacuum, one-sixth Earth \
                gravity. With nothing else to do, they default to the only thing they know — \
                setting up a platform. Unpacking servers, routing cables, configuring \
                infrastructure. The spacesuit makes everything harder: clumsy gloves, limited \
                visibility, moon dust coating every surface. Establish Nyx's confusion about the \
                mission alongside their instinct to build regardless. DO NOT mention gravity \
                problems, floating hardware, or things drifting away — that is the plot of Part 2."""
            )
        ),
        2, Map.of(
            StoryStyle.FUNNY, new Beat(
                "Problem — Servers Float Away",
                """
                The moon's gravity is only one-sixth of Earth's. Nyx's rack-mounted servers \
                aren't heavy enough to stay put — they start drifting off the rack and floating \
                away across the lunar surface. Open with Nyx's panicked dialogue as they see the \
                first server float away. Make the physical cause crystal clear: low gravity means \
                the hardware is barely held down. Then show the consequences: as servers float \
                away, pods crash, services go down, monitoring lights up red. Nyx has to \
                physically chase and wrangle floating servers in a spacesuit. Lean into what \
                makes this uniquely a moon problem — on Earth, servers just sit there. Here, \
                infrastructure has escape velocity. End on the problem — Nyx is stuck with \
                floating servers and no obvious fix.""",
                """
                In Part 1, Nyx Vasquez arrived on the moon on a mystery mission with no \
                explanation. They started setting up a developer platform on the barren lunar \
                surface — unpacking servers, running cables, configuring infrastructure in a \
                spacesuit."""
            ),
            StoryStyle.DRY, new Beat(
                "Incident Report — Hardware Displacement Under Reduced Gravity",
                """
                Open with Nyx's alarmed reaction as the first server drifts off its rack. The \
                moon's gravity — one-sixth of Earth's — is not enough to hold rack-mounted \
                hardware in place. Servers begin floating away across the lunar surface. Make the \
                cause clear: retention mechanisms designed for Earth gravity simply cannot hold \
                equipment that weighs practically nothing here. As servers drift away, the \
                consequences cascade — pods crash, services go unreachable, monitoring goes red. \
                Nyx chases floating hardware in a bulky spacesuit, grabbing one server only to \
                watch two more escape. This failure mode has no precedent on Earth. The situation \
                remains unresolved.""",
                """
                In Section 1, platform engineer Nyx Vasquez was deployed to the lunar surface \
                under undisclosed directives. No mission documentation was provided. Nyx \
                initiated standard platform provisioning: server hardware installation, cable \
                routing, and infrastructure configuration under vacuum conditions with reduced \
                gravitational acceleration."""
            )
        ),
        3, Map.of(
            StoryStyle.FUNNY, new Beat(
                "Problem — CI/CD Pipeline on Earth",
                """
                State the problem directly in the first sentence: Nyx's CI/CD pipeline runs on \
                Earth, 384,000 km away. Every git push, every build, every test has to travel to \
                Earth and back at the speed of light: ~2.5 seconds round-trip per request. Make \
                the cause unmistakable — the signal literally has to cross space. Then show the \
                consequences: hundreds of API calls in a deployment multiply those seconds into \
                hours. Lean into what makes this uniquely a moon problem — on Earth, latency is \
                milliseconds. Here, physics imposes a hard speed limit that no amount of caching \
                or optimization can fix. End on the problem — Nyx is stuck with an unusable \
                pipeline and no workaround in sight.""",
                """
                In Part 1, Nyx Vasquez arrived on the moon on a mystery mission and started \
                building a developer platform. In Part 2, the servers kept floating off the racks \
                in low gravity — one-sixth Earth's gravity means hardware doesn't stay put. Nyx \
                chased floating servers across the lunar surface while dealing with cascading pod \
                failures. That problem remains unsolved."""
            ),
            StoryStyle.DRY, new Beat(
                "Incident Report — Latency Constraints on Cislunar CI/CD Operations",
                """
                State the problem directly in the first sentence: Nyx's CI/CD pipeline \
                terminates at Earth-based infrastructure, 384,000 km distant. Every request \
                travels at the speed of light and takes about 2.5 seconds for the round trip. A \
                single deployment involves hundreds of API calls; those seconds add up to hours. \
                Make this uniquely a moon problem: on Earth, deployments take minutes. Here, \
                physics imposes a hard speed limit that no amount of optimization can fix. Nyx \
                tries everything and nothing works. The pipeline is unusable. This compounds the \
                still-unresolved floating server problem from Part 2.""",
                """
                In Section 1, Nyx Vasquez was deployed to the lunar surface and initiated \
                infrastructure provisioning. In Section 2, reduced gravitational acceleration \
                caused server hardware displacement from rack mounts, resulting in cascading \
                service failures. Manual retrieval proved inadequate. That incident remains \
                unresolved."""
            )
        ),
        4, Map.of(
            StoryStyle.FUNNY, new Beat(
                "Bittersweet Victory",
                """
                MANDATORY STRUCTURE — This part has two halves, but must still fit within the \
                ~100-word limit.

                FIRST SENTENCE (non-negotiable): Must briefly restate the two problems and \
                signal that Nyx is about to fix them. The audience has been voting and waiting \
                between parts — they need a reminder. Something like "Nyx decided it was time to \
                tackle the servers floating away and the hours-long CI/CD round trips to Earth." \
                Do NOT jump straight into solution details without this orientation beat.

                First half (brief): Then show the solutions — how they stopped the servers from \
                floating (be inventive — maybe they bolted them into the lunar bedrock, filled \
                the racks with moon rocks for ballast, or something clever) and how they solved \
                the CI/CD latency (be inventive — maybe they built a local pipeline on the moon, \
                cached everything locally, or something clever). Make the solutions satisfying \
                and specific — but keep this half short to leave room for the emotional weight.

                Second half (the heart of this part): the platform is a masterpiece — golden \
                path, internal developer portal, self-service catalog. It works perfectly. But \
                there's nobody here to use it. The moon is empty. Nyx fills out their own \
                platform satisfaction survey as sole user. Spend real time in the loneliness. \
                What's the point of building something perfect if nobody will ever use it? They \
                built something beautiful for nobody. This should ache — sit in the emptiness, \
                don't rush past it.""",
                """
                In Part 1, Nyx Vasquez arrived on the moon on a mystery mission and started \
                building a developer platform. In Part 2, servers floated away in the moon's low \
                gravity, causing cascading failures — Nyx was left chasing hardware across the \
                lunar surface with no solution. In Part 3, they discovered CI/CD ran on Earth — \
                384,000 km away — with speed-of-light delay turning every deployment into an \
                hours-long ordeal, and no workaround in sight."""
            ),
            StoryStyle.DRY, new Beat(
                "Resolution and Utilization Assessment",
                """
                MANDATORY STRUCTURE — This part has two halves, but must still fit within the \
                ~100-word limit.

                FIRST SENTENCE (non-negotiable): Must briefly restate the two problems and \
                signal that Nyx is about to resolve them. The audience has been voting and \
                waiting between parts — they need a reminder. Something like "Nyx initiated \
                remediation procedures for both the hardware displacement crisis and the \
                cislunar CI/CD latency." Do NOT jump straight into fix details without this \
                orientation beat.

                First half (brief): Then describe a specific physical fix for the floating \
                servers (anchoring to bedrock, regolith ballast, or something clever) and a \
                specific architectural fix for the CI/CD latency (local pipeline, offline sync, \
                or something clever). Keep solutions satisfying but concise.

                Second half (the heart of this part): the platform is now fully operational — \
                golden path, developer portal, self-service catalog. Everything works perfectly. \
                But the only registered user is Nyx. The moon is empty. Nyx fills out a platform \
                satisfaction survey as sole respondent. Spend real time on the emptiness — what \
                is the point of a perfect platform with no users? Document the contrast between \
                technical excellence and total isolation. This should land with weight.""",
                """
                In Section 1, Nyx Vasquez was deployed to the lunar surface and began \
                provisioning. In Section 2, reduced gravity caused hardware displacement and \
                cascading failures. In Section 3, Earth-based CI/CD infrastructure introduced \
                prohibitive latency due to speed-of-light constraints across 384,400 km. Both \
                incidents remained unresolved."""
            )
        ),
        5, Map.of(
            StoryStyle.FUNNY, new Beat(
                "Alien Reveal + Appreciation",
                """
                Alien developers emerge from somewhere on or near the moon. Be wildly creative \
                with what they look like — DO NOT default to crystalline or silicon-based beings. \
                Maybe they have tentacles, maybe their eyes are in their feet and they walk on \
                their hands, maybe they speak in pig latin, maybe they're gaseous clouds that \
                type by vibrating. Surprise us. Their computing equipment should be equally \
                strange and non-human. They discover Nyx's platform and start using it. They \
                appreciate Nyx's work in uniquely alien ways — not human gestures like handshakes \
                or thumbs up, but something creative and strange. The aliens are enthusiastic \
                users who love the golden path. Nyx finally has developers who appreciate their \
                platform. End with an over-the-top happy ending. The mystery is solved: this is \
                why Nyx was sent here.""",
                """
                In Part 1, Nyx Vasquez arrived on the moon on a mystery mission and started \
                building a developer platform. In Part 2, servers floated away in low gravity. \
                In Part 3, CI/CD ran on Earth with agonizing speed-of-light delay. In Part 4, \
                Nyx solved both problems with creative solutions, building a beautiful platform \
                with golden path, IDP, and self-service portal. But nobody was there to use it. \
                Nyx filled out their own satisfaction survey, completely alone on the perfect \
                platform."""
            ),
            StoryStyle.DRY, new Beat(
                "First Contact — Exobiological Platform Adoption",
                """
                Alien developers emerge from somewhere on or near the moon. Be wildly creative \
                with what they look like — DO NOT default to crystalline or silicon-based beings. \
                Maybe they have tentacles, maybe their eyes are in their feet and they walk on \
                their hands, maybe they speak in pig latin, maybe they're gaseous clouds that \
                type by vibrating. Surprise us. Their computing equipment should be equally \
                strange and non-human. They discover Nyx's platform and start using it. They \
                show appreciation in non-human ways — not handshakes or thumbs up, but something \
                creative and alien. They love the golden path. Nyx realizes this is why they were \
                sent here. The mission finally makes sense. End with the platform achieving its \
                intended purpose under enthusiastic alien users.""",
                """
                In Section 1, Nyx Vasquez was deployed to the lunar surface under undisclosed \
                directives and began provisioning. In Sections 2 and 3, hardware displacement \
                and CI/CD latency presented environment-specific failures. In Section 4, both \
                incidents were remediated and the platform achieved full operational status with \
                golden path, developer portal, and self-service catalog — but utilization was \
                zero, with Nyx as sole user."""
            )
        )
    );

    // -------------------------------------------------------------------------
    // Theme 2 – Circus beats (funny only)
    // -------------------------------------------------------------------------

    private static final Map<Integer, Beat> THEME2_BEATS = Map.of(
        1, new Beat(
            "Setup — The Clown Native Computing Foundation Circus",
            """
            Rae Okonkwo, backend developer, arrives at the Clown Native Computing Foundation \
            circus (spell out the full name). Describe the big top — colorful tent, sawdust \
            floor, performers warming up, clowns honking everywhere. Rae is tonight's Houdini \
            act: locked in a glass water tank in the center ring, they must deploy a working \
            app before the air in their lungs runs out. Establish the absurdity: a developer \
            who has never done anything remotely physical, underwater, trying to deploy an app \
            while holding their breath. They take one deep breath. They're lowered into the \
            tank. Water closes over them. The clock starts. The crowd leans forward."""
        ),
        2, new Beat(
            "Human Cannonball with a Helm-et",
            """
            Rae is still submerged in the center ring tank. In the ring beside them, a human \
            cannonball climbs into the cannon wearing an enormous Helm-et — make the Helm pun \
            unmistakable. This is protective headgear AND a deployment tool. They fire across \
            the big top. Through the glass, Rae watches the cannonball arc overhead while \
            making progress on the deploy: Helm charts package the application, service mesh \
            routes traffic to the right endpoints. Rae is uncomfortable — chest tightening, \
            aware they can't stay down forever — but not desperate yet. The deploy isn't done \
            yet. End on the struggle — underwater, short of breath, deploy incomplete. \
            IMPORTANT: Keep the urgency mild. No "final seconds," no "running out of time," \
            no near-death language. Save the life-threatening desperation for later parts.""",
            """
            In Part 1, Rae Okonkwo arrived at the Clown Native Computing Foundation circus and \
            was locked in a glass water tank in the center ring as the Houdini act — they must \
            deploy a working app before the air in their lungs runs out. They took a deep breath \
            and the clock started."""
        ),
        3, new Beat(
            "Trapeze Artist in Flux",
            """
            Still in the tank, Rae looks up through the water. High above the rings, a trapeze \
            artist swings back and forth — they're in Flux. Make the Flux/GitOps pun land: \
            swinging, synchronizing, always in motion. GitOps pulls the configuration into sync \
            automatically. But the app also needs to pass security and compliance checks — \
            Kyverno is the safety net strung below the trapeze. Secure and compliant by default, \
            a net that's always there whether you look down or not. Rae is getting lightheaded \
            now. The discomfort from Part 2 is growing — they're struggling but still \
            functioning, still typing through the glass. End with Rae visibly strained and \
            slowing down — closer to deploying but not there yet. IMPORTANT: Rae is struggling, \
            not dying. No "final seconds," no near-death imagery, no fading consciousness. Save \
            the edge-of-death moment for Part 4.""",
            """
            In Part 1, Rae Okonkwo was locked in a glass water tank in the center ring at the \
            Clown Native Computing Foundation circus — the Houdini act, deploy an app before \
            their air runs out. In Part 2, a human cannonball wearing a Helm-et fired across \
            the big top in an adjacent ring while Rae made progress with Helm charts and service \
            mesh routing. But they're running out of breath and the deploy isn't done."""
        ),
        4, new Beat(
            "Clown Car — One API, Many Interfaces",
            """
            Rae's vision is blurring through the tank glass. In the ring next to them, a tiny \
            clown car rolls in. An impossible number of clowns pour out, each offering a \
            different way to interact with the same Kubernetes API — one with a CLI, one with a \
            web dashboard, one waving a GitOps flag, one with an API client. One API, many \
            interfaces, all from one impossibly small car. THIS is where Rae hits near death. \
            Vision blurring, edges going dark, body going slack, on the verge of blacking out. \
            But the pieces are falling into place — the deploy is agonizingly close. End at the \
            absolute edge — Rae is about to lose consciousness, the deploy is one step from \
            completion. This is the climax of the physical tension.""",
            """
            In Part 1, Rae Okonkwo was locked in a glass water tank in the center ring at the \
            Clown Native Computing Foundation circus. In Part 2, a Helm-etted cannonball fired \
            in an adjacent ring while Helm charts and service mesh moved the deploy forward. In \
            Part 3, a trapeze artist swung in Flux overhead while GitOps synced configs and \
            Kyverno provided the security safety net. Rae is lightheaded and running out of air."""
        ),
        5, new Beat(
            "Finale — Deploy + Celebratory Circus Chaos",
            """
            The app deploys. The tank opens. Rae gasps for air as the crowd ROARS. Creative \
            freedom on what celebratory circus chaos looks like — over-the-top spectacle, \
            confetti, acrobats, impossible feats, the crowd on their feet. Must spell out "Clown \
            Native Computing Foundation" by name in the celebration. The platform made Rae look \
            like a superhero — they deployed under impossible conditions because the platform \
            handled the hard parts. Over-the-top happy ending. The crowd goes absolutely wild.""",
            """
            In Part 1, Rae Okonkwo was locked in a glass water tank in the center ring at the \
            Clown Native Computing Foundation circus. In Part 2, a Helm-etted cannonball fired \
            in an adjacent ring while Helm charts and service mesh launched the deploy. In Part \
            3, a trapeze artist in Flux overhead synced configs while Kyverno's safety net \
            handled security. In Part 4, clowns poured from a tiny car in the next ring, each \
            with a different interface to the same Kubernetes API. Rae was about to black out \
            — the deploy one step from completion."""
        )
    );

    // -------------------------------------------------------------------------
    // Shared rules per variant
    // -------------------------------------------------------------------------

    private static final List<String> SHARED_MOON = List.of(
        "Use a gender-neutral protagonist — refer to Nyx as \"they\" or \"Nyx\" throughout. Never use \"he\" or \"she\".",
        "Lead with what is physically happening. The reader should always understand the concrete situation first.",
        "Do not include a title or part number header — just write the story text directly.",
        "Do not summarize or recap earlier parts — the audience has already read them. For parts after the first, open with one bridging sentence that connects the previous part's situation to this part's action, then dive into new content.",
        "Use 2-3 short paragraphs. The audience reads on phone screens.",
        "NEVER use the phrase \"Houston, we have a problem\" or any variation of it.",
        "STRICT LIMIT on similes (comparisons using \"like\" or \"as\"): zero or one per part. Zero is preferred. Never open a paragraph with a simile."
    );

    private static final List<String> SHARED_CIRCUS = List.of(
        "Use gender-neutral language for ALL characters — refer to Rae, the cannonball, trapeze artist, clowns, and every character as \"they.\" Never use \"he\" or \"she\" for any character.",
        "Lead with what is physically happening. The reader should always understand the concrete situation first.",
        "Do not include a title or part number header — just write the story text directly.",
        "Always spell out \"Clown Native Computing Foundation\" in full — never abbreviate to CNCF, never say \"Cloud Native.\"",
        "No animal acts — no lions, elephants, or animal performers of any kind.",
        "STRICT LIMIT on similes (comparisons using \"like\" or \"as\"): zero or one per part. Zero is preferred. Never open a paragraph with a simile.",
        "Do not summarize or recap earlier parts — the audience has already read them. For parts after the first, open with one bridging sentence that connects the previous part's situation to this part's action, then dive into new content.",
        "Use 2-3 short paragraphs. The audience reads on phone screens."
    );

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Builds a Spring AI {@link Prompt} for the given story round (part number),
     * variant, and style.
     *
     * @param storyPart the part number, between 1 and {@value #TOTAL_PARTS} (inclusive)
     * @param storyTheme the story theme (MOON or CIRCUS)
     * @param storyStyle the writing style (FUNNY or DRY); DRY is only supported for MOON
     * @return a {@link Prompt} containing a system message and a user message
     */
    public Prompt buildPrompt(int storyPart, StoryTheme storyTheme, StoryStyle storyStyle) {
        if (storyPart < 1 || storyPart > TOTAL_PARTS) {
            throw new IllegalArgumentException(
                "Invalid part number: %d. Must be between 1 and %d.".formatted(storyPart, TOTAL_PARTS));
        }

        // Theme 2 (Circus) has only one style — funny
        StoryStyle effectiveStyle = storyTheme == StoryTheme.CIRCUS ? StoryStyle.FUNNY : storyStyle;

        Beat beat = resolveBeat(storyPart, storyTheme, effectiveStyle);
        String styleInstruction = resolveStyleInstruction(storyTheme, effectiveStyle);
        List<String> sharedRules = storyTheme == StoryTheme.CIRCUS ? SHARED_CIRCUS : SHARED_MOON;

        String systemText = buildSystemText(storyTheme, effectiveStyle, sharedRules, styleInstruction);
        String userText = buildUserText(storyPart, beat);

        return new Prompt(new SystemMessage(systemText), new UserMessage(userText));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Beat resolveBeat(int storyPart, StoryTheme storyTheme, StoryStyle effectiveStyle) {
        return switch (storyTheme) {
            case MOON -> {
                var styleBeats = THEME1_BEATS.get(storyPart);
                if (styleBeats == null) {
                    throw new IllegalArgumentException("No beats found for part %d".formatted(storyPart));
                }
                yield styleBeats.get(effectiveStyle);
            }
            case CIRCUS -> {
                var beat = THEME2_BEATS.get(storyPart);
                if (beat == null) {
                    throw new IllegalArgumentException("No beats found for part %d".formatted(storyPart));
                }
                yield beat;
            }
        };
    }

    private String resolveStyleInstruction(StoryTheme storyTheme, StoryStyle effectiveStyle) {
        if (storyTheme == StoryTheme.CIRCUS) {
            return STYLE_INSTRUCTION_CIRCUS;
        }
        return switch (effectiveStyle) {
            case FUNNY -> STYLE_INSTRUCTION_FUNNY;
            case DRY   -> STYLE_INSTRUCTION_DRY;
        };
    }

    private String buildSystemText(StoryTheme storyTheme, StoryStyle effectiveStyle,
                                   List<String> sharedRules, String styleInstruction) {
        var parts = new ArrayList<String>();

        // Opening persona line
        parts.add(switch (storyTheme) {
            case MOON -> switch (effectiveStyle) {
                case FUNNY -> "You are a creative storyteller writing a serialized 5-part story about Nyx Vasquez, a platform engineer on the moon.";
                case DRY   -> "You are a deadpan narrator telling a serialized 5-part story about Nyx Vasquez, a platform engineer deployed to the lunar surface. Your tone is completely serious, precise, and humorless — but you are telling a story, not writing an academic paper. The audience should follow the plot easily.";
            };
            case CIRCUS -> "You are a creative storyteller writing a serialized 5-part story about Rae Okonkwo, a backend developer performing the Houdini act at the Clown Native Computing Foundation circus.";
        });

        // Word-count hard limit
        parts.add(switch (effectiveStyle) {
            case FUNNY -> "HARD LIMIT: 100 words MAXIMUM. If your response exceeds 100 words, it will be rejected. NEVER include a word count, meta-commentary, or any text that isn't part of the story itself. Every sentence must advance the story or land a joke — cut anything that doesn't.";
            case DRY   -> "HARD LIMIT: 100 words MAXIMUM. If your response exceeds 100 words, it will be rejected. NEVER include a word count, meta-commentary, or any text that isn't part of the story itself. Every sentence must advance the narrative or establish the situation clearly — cut anything that doesn't.";
        });

        parts.addAll(sharedRules);
        parts.add(styleInstruction);

        return String.join("\n\n", parts);
    }

    private String buildUserText(int storyPart, Beat beat) {
        var parts = new ArrayList<String>();

        if (beat.priorContext() != null) {
            parts.add("## Story So Far\n\n" + beat.priorContext());
        }

        parts.add("## Part %d of %d: %s\n\n%s".formatted(storyPart, TOTAL_PARTS, beat.title(), beat.instructions()));
        parts.add("Remember: 100 words maximum. No meta-commentary about length.");

        return String.join("\n\n", parts);
    }

}
