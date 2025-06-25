import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { ArtistsService } from 'src/app/api-client/api/artists.service';
import { ArtistRequestModel } from 'src/app/api-client/model/artistRequest';

@Component({
  selector: 'app-artists-details',
  templateUrl: './artists-details.component.html',
  styleUrls: ['./artists-details.component.css']
})
export class ArtistsDetailsComponent implements OnInit {
  artist: ArtistRequestModel | null = null;
  admin: boolean = false;
  logged: boolean = false;
  id: number | null = null;
  range: string = '';
  totalArtworks: number = 0;
  currentPage: number = 1;
  artworksPage: any[] = [];
  previousPage: number | null = null;
  nextPage: number | null = null;
  search: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private artistsService: ArtistsService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = +params['id'];
      if (this.id) {
        this.loadArtist(this.id);
      }
    });
    this.route.queryParams.subscribe(params => {
      this.currentPage = +params['page'] || 1;
      this.search = params['search'] || '';
    });
  }

  loadArtist(id: number): void {
    this.artistsService.getArtist(id).subscribe({
      next: (data: ArtistRequestModel) => {
        this.artist = data;
        this.artworksPage = data.artworks || [];
        this.totalArtworks = this.artworksPage.length;
        this.range = this.artworksPage.length > 0 ? `1 - ${this.artworksPage.length}` : '0';
        this.previousPage = this.currentPage > 1 ? this.currentPage - 1 : null;
        this.nextPage = null; 
      },
      error: () => {
        this.artist = null;
        this.artworksPage = [];
        this.totalArtworks = 0;
        this.range = '0';
        this.previousPage = null;
        this.nextPage = null;
      }
    });
  }

  confirmDelete(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar este artista?')) {
    }
  }

  confirmDeleteArtwork(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar esta obra?')) {
    }
  }

  getPageParams(page: number): Params {
    return {
      page,
      search: this.search || undefined
    };
  }
}
