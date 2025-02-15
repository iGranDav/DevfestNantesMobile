import SwiftUI
import shared
import Combine

struct ContentView: View {
    @StateObject var viewModel = DevFestViewModel()
    
    @State private var selection = 0
    
    var body: some View {
        TabView(selection: $selection) {
            AgendaView(viewModel: viewModel)
                .tabItem {
                    VStack {
                        Image(systemName: "calendar")
                        Text(L10n.screenAgenda)
                    }
                }.tag(0)
            
            VenueView(viewModel: viewModel)
                .tabItem {
                    VStack {
                        Image(systemName: "location.circle.fill")
                        Text(L10n.screenVenue)
                    }
                }.tag(1)
            
            AboutView(viewModel: viewModel)
                .tabItem {
                    VStack {
                        Image(systemName: "info.circle")
                        Text(L10n.screenAbout)
                    }
                }.tag(2)
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
