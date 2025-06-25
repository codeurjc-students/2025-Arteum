import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IndexComponent } from './index/index.component';
import { LoginComponent } from './login/login.component';
import { FaqComponent } from './faq/faq.component';
import { AuthGuard } from './auth-service/auth-guard.component';
import { ErrorpageComponent } from './errorpage/errorpage.component';
import { RegisterComponent } from './register/register.component';
import { UserReviewListComponent } from './user-review-list/user-review-list.component';
import { UserDashboardComponent } from './user-dashboard/user-dashboard.component';
import { AboutComponent } from './about/about.component';
import { ContactComponent } from './contact/contact.component';
import { TermsConditionComponent } from './terms-condition/terms-condition.component';
import { PrivacyPolicyComponent } from './privacy-policy/privacy-policy.component';
import { ArtworksComponent } from './artworks/artworks.component';
import { ArtworksDetailsComponent } from './artworks-details/artworks-details.component';
import { ArtistsComponent } from './artists/artists.component';
import { ArtistsDetailsComponent } from './artists-details/artists-details.component';
import { MuseumsComponent } from './museums/museums.component';
import { MuseumsDetailsComponent } from './museums-details/museums-details.component';
import { UsersComponent } from './users/users.component';
import { UsersDetailsComponent } from './users-details/users-details.component';
import { AdminUserComponent } from './admin-user/admin-user.component';
import { UserRecommendComponent } from './user-recommend/user-recommend.component';
import { AdminArtistComponent } from './admin-artist/admin-artist.component';
import { AdminArtworkComponent } from './admin-artwork/admin-artwork.component';
import { AdminMuseumComponent } from './admin-museum/admin-museum.component';
import { AdminStatsComponent } from './admin-stats/admin-stats.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { UserFollowersComponent } from './user-followers/user-followers.component';
import { UserFollowingComponent } from './user-following/user-following.component';

export const routes: Routes = [
  /* Index */
  { path: '', component: IndexComponent },
  /* Login & Register */
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  /* User */
  { path: 'dashboard-profile', component: UserDashboardComponent, canActivate: [AuthGuard] },
  { path: 'public-profile/:id', component: UsersDetailsComponent, canActivate: [AuthGuard] },
  { path: 'user/favouritesArtworks', component: UserReviewListComponent, canActivate: [AuthGuard] },
  { path: 'user/recommended-artworks', component: UserRecommendComponent, canActivate: [AuthGuard] },
  { path: 'user/followers', component: UserFollowersComponent, canActivate: [AuthGuard] },
  { path: 'user/following', component: UserFollowingComponent, canActivate: [AuthGuard] },
  /* Admin */
  { path: 'admin/dashboard', component: AdminDashboardComponent, canActivate: [AuthGuard], data: { isAdmin: true } },
  { path: 'admin/artist/new', component: AdminArtistComponent, canActivate: [AuthGuard], data: { isAdmin: true } },
  { path: 'admin/user/new', component: AdminUserComponent, canActivate: [AuthGuard], data: { isAdmin: true } },
  { path: 'admin/artwork/new', component: AdminArtworkComponent, canActivate: [AuthGuard], data: { isAdmin: true } },
  { path: 'admin/museum/new', component: AdminMuseumComponent, canActivate: [AuthGuard], data: { isAdmin: true } },
  { path: 'admin/stats', component: AdminStatsComponent, canActivate: [AuthGuard], data: { isAdmin: true } },
  /* Artworks */
  { path: 'artworks', component: ArtworksComponent },
  { path: 'artworks/:id', component: ArtworksDetailsComponent },
  /* Museums */
  { path: 'museums', component: MuseumsComponent },
  { path: 'museum/:id', component: MuseumsDetailsComponent },
  /* Artists */
  { path: 'artists', component: ArtistsComponent },
  { path: 'artists/:id', component: ArtistsDetailsComponent },
  /* Users */
  { path: 'users', component: UsersComponent, canActivate: [AuthGuard] },
  { path: 'users/:id', component: UsersDetailsComponent, canActivate: [AuthGuard]  },
  /* FAQ */
  { path: 'faq', component: FaqComponent },
  { path: 'about', component: AboutComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'terms-condition', component: TermsConditionComponent },
  { path: 'privacy-policy', component: PrivacyPolicyComponent },
  /* Error page */
  { path: 'errorpage', component: ErrorpageComponent },
  { path: '**', redirectTo: 'errorpage', pathMatch: 'full' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})

export class AppRoutingModule { }
