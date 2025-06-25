import { NgModule, SimpleChange } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppComponent } from './app.component';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { IndexComponent } from './index/index.component';
import { CommonModule, LocationStrategy, PathLocationStrategy } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { LoginComponent } from './login/login.component';
import { FaqComponent } from './faq/faq.component';
import { ErrorpageComponent } from './errorpage/errorpage.component';
import { RegisterComponent } from './register/register.component';
import { UserReviewListComponent } from './user-review-list/user-review-list.component';
import { UserDashboardComponent } from './user-dashboard/user-dashboard.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { ArtworksComponent } from './artworks/artworks.component';
import { ArtworksDetailsComponent } from './artworks-details/artworks-details.component';
import { ArtistsComponent } from './artists/artists.component';
import { ArtistsDetailsComponent } from './artists-details/artists-details.component';
import { MuseumsComponent } from './museums/museums.component';
import { MuseumsDetailsComponent } from './museums-details/museums-details.component';
import { UsersComponent } from './users/users.component';
import { UsersDetailsComponent } from './users-details/users-details.component';
import { UserRecommendComponent } from './user-recommend/user-recommend.component';
import { AboutComponent } from './about/about.component';
import { AdminArtistComponent } from './admin-artist/admin-artist.component';
import { AdminArtworkComponent } from './admin-artwork/admin-artwork.component';
import { AdminMuseumComponent } from './admin-museum/admin-museum.component';
import { AdminStatsComponent } from './admin-stats/admin-stats.component';
import { AdminUserComponent } from './admin-user/admin-user.component';
import { ContactComponent } from './contact/contact.component';
import { PrivacyPolicyComponent } from './privacy-policy/privacy-policy.component';
import { TermsConditionComponent } from './terms-condition/terms-condition.component';
import { UserFollowersComponent } from './user-followers/user-followers.component';
import { UserFollowingComponent } from './user-following/user-following.component';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './auth-service/auth.interceptor';
@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    HeaderComponent,
    FooterComponent,
    LoginComponent,
    FaqComponent,
    RegisterComponent,
    AdminDashboardComponent,
    ArtworksComponent,
    ArtworksDetailsComponent,
    ArtistsComponent,
    ArtistsDetailsComponent,
    MuseumsComponent,
    MuseumsDetailsComponent,
    UsersComponent,
    UsersDetailsComponent,
    AdminUserComponent,
    AdminArtistComponent,
    AdminArtworkComponent,
    AdminMuseumComponent,
    AdminStatsComponent,
    AboutComponent,
    ContactComponent,
    TermsConditionComponent,
    PrivacyPolicyComponent,
    UserDashboardComponent,
    UserRecommendComponent,
    UserReviewListComponent,
    UserFollowersComponent,
    UserFollowingComponent,
    ErrorpageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    CommonModule,
    FormsModule,
    HttpClientModule,
  ],
  providers: [
    { provide: LocationStrategy, useClass: PathLocationStrategy},
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
