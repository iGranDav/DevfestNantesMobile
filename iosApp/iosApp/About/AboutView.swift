//
//  AboutView.swift
//  iosApp
//
//  Created by Stéphane Rihet on 10/07/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import URLImage

struct AboutView: View {
    @ObservedObject var viewModel: DevFestViewModel
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(UIColor.systemBackground).edgesIgnoringSafeArea(.all)
                ScrollView {
                    VStack {
                        Card {
                            VStack(spacing: 16) {
                                Image("ic_about_header")
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                Text(L10n.screenAboutHeaderBody)
                                    .foregroundColor(Color.black)
                                HStack(spacing: 24) {
                                    CustomButton(url: URL(string: "https://devfest.gdgnantes.com/code-of-conduct")!) {
                                        Text(L10n.aboutCodeOfConduct)
                                    }.foregroundColor(Color.red)
                                    CustomButton(url: URL(string: "https://devfest.gdgnantes.com/")!) {
                                        Text(L10n.aboutWebsite)
                                    }.foregroundColor(Color.red)
                                }.padding(8)
                            }.padding(8)
                        }
                        Card {
                            VStack {
                                Text(L10n.aboutSocialTitle)
                                    .frame(minWidth: 0, maxWidth: .infinity, alignment: .leading)
                                    .foregroundColor(Color.red)
                                HStack(alignment: .top, spacing: 40) {
                                    Link(destination: URL(string: "https://facebook.com/gdgnantes")!) {
                                        Image("ic_network_facebook") }
                                    Link(destination: URL(string: "https://twitter.com/gdgnantes")!) {
                                        Image("ic_network_twitter") }
                                    Link(destination: URL(string: "https://www.linkedin.com/in/gdg-nantes")!) {
                                        Image("ic_network_linkedin") }
                                    Link(destination: URL(string: "https://www.youtube.com/c/Gdg-franceBlogspotFr")!) {
                                        Image("ic_network_web") }
                                }.padding(8)
                            }.padding(8)
                        }
                        Card {
                            VStack {
                                Text(L10n.localCommunitiesTitle)
                                    .frame(minWidth: 0, maxWidth: .infinity, alignment: .leading)
                                    .foregroundColor(Color.red)
                                HStack(alignment: .top, spacing: 40) {
                                    CustomButton(url: URL(string: "https://nantes.community/")!) {
                                        Text(L10n.localCommunitiesButton)
                                    }.foregroundColor(Color.red)
                                }.padding(8)
                            }.padding(8)
                        }
                        Card {
                            VStack(spacing: 16) {
                                Text(L10n.partnersTitle)
                                    .frame(minWidth: 0, maxWidth: .infinity, alignment: .leading)
                                    .foregroundColor(Color.red)
                                
                                ForEach(self.viewModel.partnersContent, id: \.self) { category in
                                    VStack {
                                        Text(category.categoryName.name)
                                            .foregroundColor(Color.black)
                                            .bold()
                                            .padding(20)
                                        ForEach(category.partners, id: \.self) { partner in
                                            if let url = URL(string: partner.url!) {
                                                Button(action: { UIApplication.shared.open(url) }) {
                                                    if let logoUrl = URL(string: partner.logoUrl!) {
                                                        URLImage(url: logoUrl ) { image in
                                                            image
                                                                .renderingMode(.original)
                                                                .resizable()
                                                                .aspectRatio(contentMode: .fit)
                                                                .background(.white)
                                                        }
                                                    }
                                                }
                                                .frame(maxHeight: 50)
                                                .padding(8)
                                            }
                                            
                                        }
                                    }
                                }
                                Spacer(minLength: 8)
                            }.padding(8)
                        }
                    }
                    .background(Color.gray)
                    .frame(minWidth: 0, maxWidth: .infinity)
                }
                .padding(0)
                .background(Color(UIColor.systemBackground))
            }
            .task {
                await viewModel.observePartners()
            }
        }
        // must ensure that the stack navigation is used otherwise it is considered as a master view
        // and nothing is shown in the detail
        .navigationViewStyle(StackNavigationViewStyle())
        .padding(0)
    }
}

//struct AboutView_Previews: PreviewProvider {
//    static var previews: some View {
//        AboutView()
//    }
//}