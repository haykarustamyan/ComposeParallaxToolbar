# Repository guidelines for coding agents

This repository publishes `am.highapps:compose-parallax-toolbar-kmp`, a collapsing toolbar layout
for Compose Multiplatform. Most work is incremental library evolution: fixing behavior, adding a
config field or a scope modifier, and keeping the sample and docs aligned.

Using the library in an app? Read [docs/agents/SKILL.md](docs/agents/SKILL.md) instead; it is the
usage guide. This file is for changing the library itself.

## Layout of the repository

- `compose-parallax-toolbar-kmp/` is the published module. Public API lives in
  `src/commonMain/kotlin/am/highapps/parallaxtoolbar`; there is no platform-specific main code.
  Tests are in `src/commonTest` and run on Android (Robolectric), desktop and the iOS simulator.
  The ABI dumps are under `api/`.
- `sample/` is the shared playground and the compiled recipes; `sample-android`, `sample-desktop`,
  `sample-web` and `iosApp` are the hosts. `sample/README.md` explains presets and screens.
- `docs/` holds the API reference, platform guide, migration guide, generated recipes and the
  skill file. `scripts/generate-docs.py` regenerates `docs/RECIPES.md`, `llms.txt` and
  `llms-full.txt` from the recipe sources.

## How the layout works

`ComposeParallaxToolbarLayout` measures the header, body, toolbar, bottom and overlay slots in one
custom `Layout`. Collapse is driven by `HeaderScrollState`, a `ScrollableState` fed by a nested
scroll connection on the layout and a `scrollable` on the header. The canonical values are
fractions; pixel offsets derive from them. Slots read the state inside layer blocks so scrolling
never recomposes them. Read `HeaderScrollState.kt`, `ComposeParallaxToolbarLayout.kt` and
`ParallaxTopBar.kt` end to end before changing scroll, snap, stretch or placement behavior.

## Rules for changes

- Explicit API mode is on. Every public declaration needs a visibility and a return type.
- Config classes are plain immutable classes, not data classes. When adding a field, append it
  with a default and extend `copy`, `equals`, `hashCode`, `toString` and the matching
  `ParallaxToolbarDefaults` factory.
- Invalid values fail at construction with a `require` whose message names the field and range.
- A public API change must update the ABI dumps (`updateKotlinAbi`), `docs/API.md`, the skill
  file, `CHANGELOG.md` and, when relevant, a recipe.
- Add a test for every behavior change in `commonTest`; the coverage gate is 95 percent.
- Do not add dependencies beyond Compose UI and Foundation to the library module.

## Verification

```bash
./gradlew :compose-parallax-toolbar-kmp:desktopTest :compose-parallax-toolbar-kmp:testAndroidHostTest
./gradlew :compose-parallax-toolbar-kmp:iosSimulatorArm64Test
./gradlew ktlintFormat :compose-parallax-toolbar-kmp:checkKotlinAbi :compose-parallax-toolbar-kmp:koverVerify
./gradlew :sample-android:assembleDebug :sample-desktop:compileKotlin :sample-web:wasmJsBrowserDistribution
python3 scripts/generate-docs.py
```

Run the first and third lines for any code change; the others when the change touches what they
cover. The playground on any host is the place to check a behavior change by eye; `?preset=` on
the web host and the second argument of the desktop host select a preset.
