//
//  AgendaView.swift
//  iosApp
//
//  Created by Stéphane Rihet on 10/07/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct AgendaView: View {
    @ObservedObject var viewModel: DevFestViewModel
    
    @State private var day = "2022-10-20"
    @State private var showFavoritesOnly = false
    @State private var selectedRoom: Room?
    @State private var selectedComplexity: Complexity?
    @State private var selectedLanguage: SessionLanguage?
    @State private var selectedSessionType: SessionType?
    @State private var clearFilter = false
        
    var sectionTimeFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter
    }()
    
    var body: some View {

            NavigationView {
                LoadingView(isShowing: $viewModel.isLoading) {
                VStack {
                    Picker("What is the day?", selection: $day) {
                        Text(L10n.day1).tag("2022-10-20")
                        Text(L10n.day2).tag("2022-10-21")
                    }
                    .pickerStyle(.segmented)
                    List {
                        ForEach(viewModel.agendaContent.sections.filter{($0.day.contains(day))}, id: \.date) { section in
                            Section(header: Text("\(self.sectionTimeFormatter.string(from: section.date))")) {
                                let filteredSessions = getFilteredSessions(sessions: section.sessions)
                                ForEach(self.showFavoritesOnly ? filteredSessions.filter({viewModel.favorites.contains($0.id)}):  filteredSessions, id: \.id) { session in
                                    if session.sessionType == .conference || session.sessionType == .codelab || session.sessionType == .quickie{
                                        NavigationLink(destination: AgendaDetailView(session: session, viewModel: viewModel, day: day)) {
                                            AgendaCellView(viewModel: viewModel, session: session)
                                        }
                                    } else {
                                        AgendaCellView(viewModel: viewModel, session: session)
                                    }
                                    
                                }
                            }
                        }
                    }
                    .navigationBarTitle(L10n.screenAgenda)
                    .navigationBarItems(trailing:
                                            Menu("\(Image(systemName: "line.3.horizontal.decrease.circle"))") {
                        let selected = Binding(
                            get: { self.showFavoritesOnly },
                            set: { self.showFavoritesOnly = $0 == self.showFavoritesOnly ? false : $0 }
                        )
                        Picker("", selection: selected) {
                            Text(L10n.filterFavorites).tag(true)
                            Menu(L10n.filterLanguage) {
                                let selected = Binding(
                                    get: { self.selectedLanguage },
                                    set: { self.selectedLanguage = $0 == self.selectedLanguage ? nil : $0 })
                                Picker(L10n.filterLanguage, selection: selected) {
                                    Text(L10n.languageFrench).tag(Optional(SessionLanguage.french))
                                    Text(L10n.languageEnglish).tag(Optional(SessionLanguage.english))
                                }
                            }
                            Menu(L10n.filterComplexity) {
                                let selected = Binding(
                                    get: { self.selectedComplexity },
                                    set: { self.selectedComplexity = $0 == self.selectedComplexity ? nil : $0 })
                                Picker(L10n.filterComplexity, selection: selected) {
                                    Text(L10n.complexityBeginer).tag(Optional(Complexity.beginner))
                                    Text(L10n.complexityIntermediate).tag(Optional(Complexity.intermediate))
                                    Text(L10n.complexityAdvanced).tag(Optional(Complexity.advanced))
                                }
                            }
                            Menu(L10n.filterRooms) {
                                let selected = Binding(
                                    get: { self.selectedRoom },
                                    set: { self.selectedRoom = $0 == self.selectedRoom ? nil : $0 })
                                Picker(L10n.filterRooms, selection: selected) {
                                    ForEach(self.viewModel.roomsContent, id: \.id) { room in
                                        Text(room.name).tag(Optional(room))
                                    }
                                }
                            }
                            Menu(L10n.filterSessionType) {
                                let selected = Binding(
                                    get: { self.selectedSessionType },
                                    set: { self.selectedSessionType = $0 == self.selectedSessionType ? nil : $0 })
                                Picker(L10n.filterSessionType, selection: selected) {
                                    Text(L10n.sessionTypeConference).tag(Optional(SessionType.conference))
                                    Text(L10n.sessionTypeQuickie).tag(Optional(SessionType.quickie))
                                    Text(L10n.sessionTypeCodelab).tag(Optional(SessionType.codelab))
                                }
                            }
                        }
                        if showFavoritesOnly || selectedRoom != nil || selectedLanguage != nil || selectedComplexity != nil || selectedSessionType != nil {
                            Button(action: {
                                self.showFavoritesOnly = false
                                self.selectedRoom = nil
                                self.selectedLanguage = nil
                                self.selectedSessionType = nil
                                self.selectedComplexity = nil
                            }) {
                                Label(L10n.filterClear, systemImage: "trash")
                            }
                        }

                    })
                    .task {
                        RCValues.sharedInstance.fetchCloudValues()
                        await viewModel.observeRooms()
                        await viewModel.observeSessions()
                    }
                }
            }
        }
            .onAppear{
                FirebaseAnalyticsService.shared.pageEvent(page: AnalyticsPage.agenda, className: "AgendaView")
            }
    }
    
    func getFilteredSessions(sessions: [AgendaContent.Session]) -> [AgendaContent.Session]{
        var selectedSession: [AgendaContent.Session] = sessions
        if let unwrappedLanguage = selectedLanguage {
            selectedSession = selectedSession.filter {
                $0.language == unwrappedLanguage
                
            }
        }
        if let unwrappedSessiontype = selectedSessionType {
            selectedSession = selectedSession.filter {
                $0.sessionType == unwrappedSessiontype
                
            }
        }
        if let unwrappedRooms = selectedRoom {
            selectedSession = selectedSession.filter({unwrappedRooms.name.contains($0.room)})
        }
        if let unwrappedComplexity = selectedComplexity {
            selectedSession = selectedSession.filter {
                $0.complexity == unwrappedComplexity
                
            }
        }
        return selectedSession
    }
    
}

//struct AgendaView_Previews: PreviewProvider {
//    static var previews: some View {
//        AgendaView()
//    }
//}
