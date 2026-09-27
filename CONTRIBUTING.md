# Contributing

Thanks for helping. Bug reports, feature requests and pull requests are all welcome.

## Reporting

- **Bugs**: open an issue with the library version, platform, and steps or a snippet that
  reproduces it. The issue template asks for exactly that.
- **Features**: open an issue describing the use case before a large change, so we can agree on
  the API first.
- **Security**: see [SECURITY.md](SECURITY.md); do not open a public issue.

## Setup

Requirements: JDK 21 or newer, Android SDK with API 37, and Xcode for the iOS targets.

```bash
git clone https://github.com/haykarustamyan/ComposeParallaxToolbar.git
cd ComposeParallaxToolbar
./gradlew :compose-parallax-toolbar-kmp:testAndroidHostTest :compose-parallax-toolbar-kmp:desktopTest
```

Modules:

| Module | Purpose |
|---|---|
| `compose-parallax-toolbar-kmp` | The library |
| `sample` | Shared playground and sample screens |
| `sample-android`, `sample-desktop`, `sample-web`, `iosApp` | Hosts for the playground |

## Tests

The Compose UI tests live in `commonTest` and run on three targets. Run what your change touches;
CI runs all of them.

```bash
./gradlew :compose-parallax-toolbar-kmp:testAndroidHostTest        # Robolectric
./gradlew :compose-parallax-toolbar-kmp:desktopTest                # JVM
./gradlew :compose-parallax-toolbar-kmp:iosSimulatorArm64Test      # needs a booted simulator
./gradlew :compose-parallax-toolbar-kmp:koverVerify                # 95% line coverage gate
```

iOS is where platform differences show up, so a change to scrolling or layout should run there
at least once.

## Public API

The library uses explicit API mode and a checked-in ABI dump under
`compose-parallax-toolbar-kmp/api/`. When a change to the public surface is intended:

```bash
./gradlew :compose-parallax-toolbar-kmp:updateKotlinAbi
```

Commit the updated dump with a `CHANGELOG.md` entry. CI runs `checkKotlinAbi` and fails on any
unrecorded change. Configuration types are plain classes on purpose; when adding a field, append
it with a default, extend `equals`, `hashCode`, `toString` and `copy`, and keep the previous
`copy` overload.

## Generated docs

`docs/RECIPES.md`, `llms.txt` and `llms-full.txt` are generated. After changing a recipe in
`sample/src/*/recipes/` or any guide under `docs/`, run:

```bash
python3 scripts/generate-docs.py
```

and commit the output; the Docs workflow fails when it is stale.

## Pull requests

1. Branch from `main`.
2. Keep the change focused; add or update tests; update the docs that describe the behavior.
3. Add a line to the top version section of `CHANGELOG.md` under Added, Changed, Removed or Fixed.
4. Make sure the tests above pass, then open the pull request. The template lists what to include.

## Releasing

1. Set the version in `compose-parallax-toolbar-kmp/build.gradle.kts` and the README install snippet.
2. Add a `## x.y.z` section at the top of `CHANGELOG.md`.
3. Commit, then push a tag: `git tag vx.y.z && git push origin vx.y.z`.

The Release workflow refuses a tag whose version does not match the build file or has no
changelog section, publishes to Maven Central, and then creates the GitHub release with that
changelog section as its notes. A tag with a suffix, such as `v2.1.0-rc1`, is marked as a
pre-release.
