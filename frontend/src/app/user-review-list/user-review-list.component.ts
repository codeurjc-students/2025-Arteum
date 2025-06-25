import { Component, OnInit } from '@angular/core';
import { UsersService } from '../api-client/api/users.service';

@Component({
  selector: 'app-user-review-list',
  templateUrl: './user-review-list.component.html',
  styleUrls: ['./user-review-list.component.css']
})
export class UserReviewListComponent implements OnInit {
  userName: string = '';
  artworksEmpty: boolean = true;
  sortValue: string = '';
  topArtists: any[] = [];
  search: string = '';
  ratingRanges: any[] = [];
  range: string = '';
  totalArtworks: number = 0;
  sortTitle: boolean = false;
  sortRating: boolean = false;
  sortArtist: boolean = false;
  sortYear: boolean = false;
  sortMuseum: boolean = false;
  currentPage: number = 1;
  artworksPage: any[] = [];
  logged: boolean = false;
  admin: boolean = false;
  previousPage: number | null = null;
  nextPage: number | null = null;

  constructor(private usersService: UsersService) {}

  ngOnInit(): void {
    this.loadFavouritesArtworks();
  }

  loadFavouritesArtworks(): void {
    this.usersService.getFavouritesArtworks(this.currentPage, 12, this.search, this.sortValue)
      .subscribe({
        next: (artworks) => {
          this.artworksPage = artworks || [];
          this.artworksEmpty = this.artworksPage.length === 0;
          this.totalArtworks = this.artworksPage.length;
        },
        error: () => {
          this.artworksPage = [];
          this.artworksEmpty = true;
        }
      });
  }

  onArtistFilterChange(event: Event) {
    this.loadFavouritesArtworks();
  }

  onRatingRangeChange(event: Event) {
    this.loadFavouritesArtworks();
  }

  onSortChange(event: Event) {
    this.loadFavouritesArtworks();
  }

  confirmDeleteArtwork(event: MouseEvent) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta obra?')) {
      event.preventDefault();
    }
  }
}