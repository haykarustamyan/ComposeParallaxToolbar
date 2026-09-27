import SwiftUI
import SampleShared

/// Hosts the shared Compose playground. Set SAMPLE_SCREEN in the scheme's environment
/// to open a fixed sample screen instead, for example "lazyPadding".
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let env = ProcessInfo.processInfo.environment
        return MainViewControllerKt.MainViewController(screen: env["SAMPLE_SCREEN"] ?? "playground", preset: env["SAMPLE_PRESET"] ?? "default")
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}
