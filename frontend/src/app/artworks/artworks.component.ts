import { Component, OnInit } from '@angular/core';
import { ArtworksService } from 'src/app/api-client/api/artworks.service';
import { ArtworkRequestModel } from 'src/app/api-client/model/artworkRequest';
import { ActivatedRoute, Params, Router } from '@angular/router';

@Component({
  selector: 'app-artworks',
  templateUrl: './artworks.component.html',
  styleUrls: ['./artworks.component.css']
})
export class ArtworksComponent implements OnInit {
  error: string | null = null;
  success: string | null = null;

  sortTitle = false;
  sortRating = false;
  sortArtist = false;
  sortYear = false;
  sortMuseum = false;
  search: string = '';
  topArtists: { id: number, name: string, selected: boolean }[] = [];
  ratingRanges: { min: number, max: number, selected: boolean }[] = [
    { min: 0, max: 2, selected: false },
    { min: 2, max: 4, selected: false },
    { min: 4, max: 5, selected: false }
  ];

  artworksEmpty = false;
  artworksPage: ArtworkRequestModel[] = [];
  range: string = '';
  totalArtworks: number = 0;
  currentPage: number = 1;
  previousPage: number | null = null;
  nextPage: number | null = null;

  logged = false;
  admin = false;

  constructor(
    private artworksService: ArtworksService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.initFiltersFromParams(params);
      this.loadArtworks();
    });
  }

  initFiltersFromParams(params: Params) {
    this.currentPage = +params['page'] || 1;
    this.search = params['search'] || '';
    const sort = params['sort'] || '';
    this.sortTitle = sort === 'title';
    this.sortRating = sort === 'averageRating';
    this.sortArtist = sort === 'artist.name';
    this.sortYear = sort === 'creationYear';
    this.sortMuseum = sort === 'museum.name';

    const artistParam = params['artist'];
    let selectedArtists: number[] = [];
    if (artistParam) {
      if (Array.isArray(artistParam)) {
        selectedArtists = artistParam.map(Number);
      } else {
        selectedArtists = [Number(artistParam)];
      }
    }
    this.topArtists = [
      { id: 1, name: 'Artista 1', selected: selectedArtists.includes(1) },
      { id: 2, name: 'Artista 2', selected: selectedArtists.includes(2) },
      { id: 3, name: 'Artista 3', selected: selectedArtists.includes(3) }
    ];

    const ratingParam = params['ratingRanges'];
    let selectedRanges: string[] = [];
    if (ratingParam) {
      if (Array.isArray(ratingParam)) {
        selectedRanges = ratingParam;
      } else {
        selectedRanges = [ratingParam];
      }
    }
    this.ratingRanges.forEach(r => {
      r.selected = selectedRanges.includes(`${r.min}-${r.max}`);
    });
  }

  loadArtworks(): void {
    const page = this.currentPage;
    const size = 9;
    const sort = this.sortTitle ? 'title' :
                this.sortRating ? 'averageRating' :
                this.sortArtist ? 'artist.name' :
                this.sortYear ? 'creationYear' :
                this.sortMuseum ? 'museum.name' : '';
    const artistIds = this.topArtists.filter(a => a.selected).map(a => a.id);
    const ratingRanges = this.ratingRanges.filter(r => r.selected).map(r => `${r.min}-${r.max}`);

    this.artworksService.getArtworks(
      page,
      size,
      this.search || undefined,
      sort || undefined,
      artistIds.length ? artistIds : undefined,
      ratingRanges.length ? ratingRanges : undefined
    ).subscribe({
      next: (data: any) => {
        this.artworksPage = data.content;
        this.artworksEmpty = data.content.length === 0;
        this.totalArtworks = data.totalElements;
        this.range = `${(page - 1) * size + 1} - ${(page - 1) * size + data.content.length}`;
        this.previousPage = page > 1 ? page - 1 : null;
        this.nextPage = data.content.length === size ? page + 1 : null;
      },
      error: (err) => {
        this.error = 'Error al cargar las obras de arte';
        this.artworksPage = [];
        this.artworksEmpty = true;
      }
    });
  }

  onArtistChange(artist: any) {
    artist.selected = !artist.selected;
    this.navigateWithFilters();
  }

  onRatingRangeChange(range: any) {
    range.selected = !range.selected;
    this.navigateWithFilters();
  }

  onSortChange(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.sortTitle = value === 'title';
    this.sortRating = value === 'averageRating';
    this.sortArtist = value === 'artist.name';
    this.sortYear = value === 'creationYear';
    this.sortMuseum = value === 'museum.name';
    this.navigateWithFilters();
  }

  confirmDelete(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar esta obra?')) {
      this.success = 'Obra eliminada correctamente (simulado)';
      this.loadArtworks();
    }
  }

  getPageParams(page: number): Params {
    return {
      page,
      sort: this.sortTitle ? 'title' : this.sortRating ? 'averageRating' : this.sortArtist ? 'artist.name' : this.sortYear ? 'creationYear' : this.sortMuseum ? 'museum.name' : '',
      search: this.search || undefined,
      artist: this.topArtists.filter(a => a.selected).map(a => a.id),
      ratingRanges: this.ratingRanges.filter(r => r.selected).map(r => `${r.min}-${r.max}`)
    };
  }

  private navigateWithFilters() {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: this.getPageParams(1),
      queryParamsHandling: 'merge'
    });
  }

  onSearchSubmit() {
    this.currentPage = 1;
    this.navigateWithFilters();
  }
}
