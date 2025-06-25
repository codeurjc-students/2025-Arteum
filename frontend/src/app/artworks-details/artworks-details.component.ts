import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ArtworksService } from 'src/app/api-client/api/artworks.service';
import { ArtworkRequestModel } from 'src/app/api-client/model/artworkRequest';

@Component({
  selector: 'app-artworks-details',
  templateUrl: './artworks-details.component.html',
  styleUrls: ['./artworks-details.component.css']
})
export class ArtworksDetailsComponent implements OnInit {
  artwork: ArtworkRequestModel | null = null;
  currentPage: number = 1;
  logged: boolean = false;
  admin: boolean = false;
  isFavoriteArtwork: boolean = false;
  reviewsEmpty: boolean = true;
  reviewsPage: any[] = [];
  find7RandomArtworks: ArtworkRequestModel[] = [];
  newReview: any = { rating: null, comment: '' };
  userReview: any = null;
  range: string = '';
  totalReviews: number = 0;
  sort: string = 'createdAt';
  sortCreatedAt: boolean = true;
  sortRating: boolean = false;
  sortRatingAsc: boolean = false;
  sortCreatedAtAsc: boolean = false;
  previousPage: number | null = null;
  nextPage: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private artworksService: ArtworksService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      this.loadArtwork(id);
      this.loadSimilarArtworks();
      this.loadReviews(id);
    });
    this.route.queryParams.subscribe(params => {
      this.currentPage = +params['page'] || 1;
      this.sort = params['sort'] || 'createdAt';
      this.updateSortFlags();
    });
  }

  loadArtwork(id: number) {
    this.artworksService.getArtworkById(id).subscribe({
      next: (data: ArtworkRequestModel) => {
        this.artwork = data;
      },
      error: () => {
        this.artwork = null;
      }
    });
  }

  loadSimilarArtworks() {
    this.artworksService.getRandomArtworks().subscribe({
      next: (data: ArtworkRequestModel[]) => {
        this.find7RandomArtworks = data;
      }
    });
  }

  loadReviews(id: number) {
    this.reviewsPage = [];
    this.totalReviews = 0;
    this.range = '';
    this.reviewsEmpty = this.reviewsPage.length === 0;
    this.previousPage = this.currentPage > 1 ? this.currentPage - 1 : null;
    this.nextPage = null;
  }

  addReview() {
  }

  editReview() {
  }

  getStarTitle(star: number): string {
    switch (star) {
      case 5: return 'De lo mejor que he visto';
      case 4: return 'Está muy bien';
      case 3: return 'Normal';
      case 2: return 'Malo';
      case 1: return 'Muy malo';
      default: return '';
    }
  }

  confirmDelete(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar esta obra de arte?')) {
    }
  }

  confirmDeleteReview(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar esta review?')) {
    }
  }

  onSortReviews(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.sort = value;
    this.updateSortFlags();
    if (this.artwork) {
      this.loadReviews(this.artwork.id as number);
    }
  }

  updateSortFlags() {
    this.sortCreatedAt = this.sort === 'createdAt';
    this.sortRating = this.sort === 'rating';
    this.sortRatingAsc = this.sort === 'ratingAsc';
    this.sortCreatedAtAsc = this.sort === 'createdAtAsc';
  }
}
