#!/usr/bin/env python3
"""Generates docs/RECIPES.md from the compiled recipe sources, and llms.txt / llms-full.txt
from the markdown docs. Run from the repository root; CI checks the output is committed."""
import re, pathlib

ROOT = pathlib.Path(__file__).resolve().parent.parent
RECIPE_SOURCES = [
    ROOT / "sample/src/commonMain/kotlin/am/highapps/parallaxtoolbar/sample/recipes/Recipes.kt",
    ROOT / "sample/src/iosMain/kotlin/am/highapps/parallaxtoolbar/sample/recipes/IosHosting.kt",
]
SWIFT_HOST = '''struct PlaylistView: UIViewControllerRepresentable {
    let items: [String]
    func makeUIViewController(context: Context) -> UIViewController {
        IosHostingKt.PlaylistViewController(items: items)
    }
    func updateUIViewController(_ vc: UIViewController, context: Context) {}
}

// Somewhere in your SwiftUI hierarchy:
PlaylistView(items: tracks).ignoresSafeArea()'''

def version():
    text = (ROOT / "compose-parallax-toolbar-kmp/build.gradle.kts").read_text()
    return re.search(r'version = "([^"]+)"', text).group(1)

def recipes():
    out = []
    for src in RECIPE_SOURCES:
        text = src.read_text()
        for m in re.finditer(r"// recipe: ([^\n]+)\n((?:// [^\n]*\n)*)(.*?)// end recipe", text, re.S):
            title, comment, code = m.group(1).strip(), m.group(2), m.group(3).rstrip()
            desc = " ".join(line[3:].strip() for line in comment.splitlines())
            out.append((title, desc, code))
    return out

def write_recipes():
    v = version()
    lines = [
        "# Recipes",
        "",
        f"Complete screens for common tasks, version {v}. Every Kotlin block is compiled as part of the",
        "`sample` module on each CI run, so it is known to build against the current API. Imports are in",
        "[Recipes.kt](../sample/src/commonMain/kotlin/am/highapps/parallaxtoolbar/sample/recipes/Recipes.kt);",
        "the examples use Material 3 for their own widgets, which the library does not require.",
        "",
    ]
    for title, desc, code in recipes():
        lines += [f"## {title}", "", desc, "", "```kotlin", code, "```", ""]
        if title == "iOS hosting":
            lines += ["Swift side:", "", "```swift", SWIFT_HOST, "```", "",
                      "Add `CADisableMinimumFrameDurationOnPhone = true` to the app's `Info.plist`; Compose refuses to",
                      "start on high refresh rate iPhones without it.", ""]
    (ROOT / "docs/RECIPES.md").write_text("\n".join(lines))

def write_llms():
    v = version()
    base = "https://github.com/haykarustamyan/ComposeParallaxToolbar/blob/main/"
    short = f"""# ComposeParallaxToolbar

> Collapsing toolbar with a parallax header for Compose Multiplatform (Android, iOS, desktop, web). Version {v}. Maven coordinate `am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp:{v}`. Depends only on Compose UI and Foundation; works with any design system.

Mental model: one composable, `ComposeParallaxToolbarLayout`, with slots (`titleContent`, `headerContent`, `content`, optional `subtitleContent`, `navigationIcon`, `actions`, `overlayContent`, `bottomContent`). The body is a `ParallaxContent` (`Regular`, `Lazy`, or `Custom` for any scrollable). The header collapses through nested scrolling. Behavior is set through immutable configs built by `ParallaxToolbarDefaults`. State is hoisted in `rememberParallaxToolbarState()`; every slot runs in a `ParallaxToolbarScope` with `collapseFraction` and per-element modifiers.

## Docs

- [API reference]({base}docs/API.md): every parameter, config field, default, state member and modifier.
- [Recipes]({base}docs/RECIPES.md): complete compiling screens for common tasks.
- [Platform guide]({base}docs/PLATFORMS.md): Android edge-to-edge and Scaffold, iOS hosting and the required Info.plist key, desktop, web.
- [Migration from 1.x]({base}docs/MIGRATION.md).
- [Agent skill]({base}docs/agents/SKILL.md): condensed usage rules for coding assistants.
- [Changelog]({base}CHANGELOG.md).

## Optional

- [README]({base}README.md)
- [Full docs in one file]({base}llms-full.txt)
"""
    (ROOT / "llms.txt").write_text(short)
    parts = [short, ""]
    for rel in ["docs/agents/SKILL.md", "docs/API.md", "docs/RECIPES.md", "docs/PLATFORMS.md", "docs/MIGRATION.md", "CHANGELOG.md"]:
        parts += [f"\n\n---\n\n<!-- {rel} -->\n", (ROOT / rel).read_text()]
    (ROOT / "llms-full.txt").write_text("".join(parts))

if __name__ == "__main__":
    write_recipes()
    write_llms()
    print("generated docs/RECIPES.md, llms.txt, llms-full.txt")
