import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UsersService } from '../api-client/api/users.service';

@Component({
  selector: 'app-users-details',
  templateUrl: './users-details.component.html',
  styleUrls: ['./users-details.component.css']
})
export class UsersDetailsComponent implements OnInit {
  name: string = '';
  username: string = '';
  admin: boolean = false;
  logged: boolean = false;
  isNotMyProfile: boolean = false;
  isFollowing: boolean = false;
  id: number | string = '';
  location: string = '';
  biography: string = '';
  reviews: any[] = [];
  reviews_avg: number = 0;
  followers: number = 0;
  follows: number = 0;
  artworksEmpty: boolean = true;
  artworks: any[] = [];

  constructor(
    private usersService: UsersService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.username = params['username'] || '';
      if (this.username) {
        this.loadPublicProfile(this.username);
      }
    });
  }

  loadPublicProfile(username: string): void {
    this.usersService.getPublicProfile(username).subscribe({
      next: (profile) => {
        this.name = profile.name || '';
        this.username = profile.username || '';
        this.location = profile.location || '';
        this.biography = profile.biography || '';
        this.reviews = profile.reviews || [];
        this.reviews_avg = profile.reviews_avg || 0;
        this.followers = profile.followers || 0;
        this.follows = profile.follows || 0;
        this.artworks = profile.artworks || [];
        this.artworksEmpty = !this.artworks || this.artworks.length === 0;
        this.admin = !!profile.admin;
        this.logged = !!profile.logged;
        this.isNotMyProfile = !!profile.isNotMyProfile;
        this.isFollowing = !!profile.isFollowing;
        this.id = profile.id || '';
      },
      error: () => {
        this.artworks = [];
        this.artworksEmpty = true;
      }
    });
  }

  confirmDelete(arg0: any) {
  }

  confirmDeleteArtwork(arg0: any) {
  }
}
