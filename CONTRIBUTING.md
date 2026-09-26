# Contributing to ComposeParallaxToolbar

Thank you for your interest in contributing! Here's how you can help:

## Ways to Contribute

### Reporting Bugs
- Create an issue with a clear title
- Describe how to reproduce the bug
- Include device info and library version

### Feature Requests
- Create an issue with "[Feature]" in the title
- Describe what you need and why it's useful

### Pull Requests

1. Fork the repository
2. Create a new branch
3. Make your changes
4. Submit a pull request

## Development Setup

1. Clone the repository
2. Open in Android Studio or IntelliJ IDEA
3. Build the project

## Guidelines

- Follow Kotlin coding conventions
- Test your changes thoroughly
- Update documentation if needed

## Questions?

Please correct any failures before requesting a review.
Feel free to open an issue for any questions.

Thank you for your contributions! 

## Public API changes

The library uses explicit API mode and a checked-in ABI dump under
`compose-parallax-toolbar-kmp/api/`. If a change touches the public surface on purpose, run

```bash
./gradlew :compose-parallax-toolbar-kmp:updateKotlinAbi
```

and commit the updated dump together with a CHANGELOG entry. CI runs `checkKotlinAbi` and fails on
any unrecorded change.

## Releasing

1. Set the new version in `compose-parallax-toolbar-kmp/build.gradle.kts` and in the README
   install snippets.
2. Add a `## x.y.z` section at the top of `CHANGELOG.md`.
3. Commit, then push a tag: `git tag vx.y.z && git push origin vx.y.z`.

The Release workflow refuses a tag whose version does not match the build file or has no
changelog section, publishes to Maven Central, and creates the GitHub release with the changelog
section as notes. A tag with a suffix, such as `v2.1.0-rc1`, is marked as a pre-release.
