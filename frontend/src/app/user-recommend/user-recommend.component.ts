import { Component, OnInit } from '@angular/core';
import { UsersService } from '../api-client/api/users.service';

@Component({
  selector: 'app-user-recommend',
  templateUrl: './user-recommend.component.html',
  styleUrls: ['./user-recommend.component.css']
})
export class UserRecommendComponent implements OnInit {
  recommendedArtworksEmpty: boolean = true;
  recommendedArtworks: any[] = [];
  logged: boolean = false;
  admin: boolean = false;

  constructor(private usersService: UsersService) {}

  ngOnInit(): void {
    this.loadRecommendedArtworks();
  }

  loadRecommendedArtworks(): void {
    this.usersService.getRecommendedArtworks().subscribe({
      next: (artworks) => {
        this.recommendedArtworks = artworks || [];
        this.recommendedArtworksEmpty = this.recommendedArtworks.length === 0;
      },
      error: () => {
        this.recommendedArtworks = [];
        this.recommendedArtworksEmpty = true;
      }
    });
  }

  confirmDeleteArtwork(event: MouseEvent) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta obra?')) {
      event.preventDefault();
    }
  }
}
