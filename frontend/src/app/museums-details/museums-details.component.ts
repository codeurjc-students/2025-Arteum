import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { MuseumsService } from 'src/app/api-client/api/museums.service';
import { MuseumRequestModel } from 'src/app/api-client/model/museumRequest';

@Component({
  selector: 'app-museums-details',
  templateUrl: './museums-details.component.html',
  styleUrls: ['./museums-details.component.css']
})
export class MuseumsDetailsComponent implements OnInit {
  museum: MuseumRequestModel | null = null;
  admin: boolean = false;
  id: number | null = null;
  logged: boolean = false;
  error: string | null = null;
  success: string | null = null;
  range: string = '';
  totalArtworks: number = 0;
  artworksEmpty: boolean = false;
  artworksPage: any[] = [];
  previousPage: number | null = null;
  currentPage: number = 1;
  nextPage: number | null = null;
  search: undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private museumsService: MuseumsService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = +params['id'];
      if (this.id) {
        this.loadMuseum(this.id);
      }
    });
    this.route.queryParams.subscribe(params => {
      this.currentPage = +params['page'] || 1;
      this.search = params['search'] || '';
    });
  }

  loadMuseum(id: number): void {
    this.museumsService.getMuseumById(id).subscribe({
      next: (data: MuseumRequestModel) => {
        console.debug('Museum loaded:', data);
        this.museum = data;
        this.artworksPage = data.artworks || [];
        this.totalArtworks = this.artworksPage.length;
        this.artworksEmpty = this.artworksPage.length === 0;
        this.range = this.artworksPage.length > 0 ? `1 - ${this.artworksPage.length}` : '0';
        this.previousPage = this.currentPage > 1 ? this.currentPage - 1 : null;
        this.nextPage = null;
      },
      error: (err) => {
        console.error('Error loading museum:', err);
        this.museum = null;
        this.artworksPage = [];
        this.totalArtworks = 0;
        this.artworksEmpty = true;
        this.range = '0';
        this.previousPage = null;
        this.nextPage = null;
      }
    });
  }

  confirmDelete(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar este museo?')) {
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