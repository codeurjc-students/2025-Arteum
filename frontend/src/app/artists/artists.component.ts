import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ArtistsService } from 'src/app/api-client/api/artists.service';
import { ArtistRequestModel } from 'src/app/api-client/model/artistRequest';

@Component({
  selector: 'app-artists',
  templateUrl: './artists.component.html',
  styleUrls: ['./artists.component.css']
})
export class ArtistsComponent implements OnInit {
  error: string | null = null;
  success: string | null = null;

  sortName = false;
  sortBirthDate = false;
  sortNationality = false;
  sort: string = 'name';
  search: string = '';
  nationalityFilters: { name: string, selected: boolean }[] = [];
  centuryFilters: { century: number, label: string, selected: boolean }[] = [];

  artistsEmpty = false;
  artistsPage: ArtistRequestModel[] = [];
  range: string = '';
  totalArtists: number = 0;
  currentPage: number = 1;
  previousPage: number | null = null;
  nextPage: number | null = null;

  admin = false;

  constructor(
    private artistsService: ArtistsService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.initFiltersFromParams(params);
      this.loadArtists();
    });
  }

  initFiltersFromParams(params: any) {
    this.currentPage = +params['page'] || 1;
    this.search = params['search'] || '';
    const sort = params['sort'] || 'name';
    this.sort = sort;
    this.sortName = sort === 'name';
    this.sortBirthDate = sort === 'dateOfBirth';
    this.sortNationality = sort === 'nationality';

    const natParam = params['nationalityFilters'];
    let selectedNats: string[] = [];
    if (natParam) {
      if (Array.isArray(natParam)) {
        selectedNats = natParam;
      } else {
        selectedNats = [natParam];
      }
    }
    this.nationalityFilters = [
      { name: 'Española', selected: selectedNats.includes('Española') },
      { name: 'Francesa', selected: selectedNats.includes('Francesa') },
      { name: 'Italiana', selected: selectedNats.includes('Italiana') }
    ];

    const centParam = params['centuries'];
    let selectedCents: number[] = [];
    if (centParam) {
      if (Array.isArray(centParam)) {
        selectedCents = centParam.map(Number);
      } else {
        selectedCents = [Number(centParam)];
      }
    }
    this.centuryFilters = [
      { century: 18, label: 'Siglo XVIII', selected: selectedCents.includes(18) },
      { century: 19, label: 'Siglo XIX', selected: selectedCents.includes(19) },
      { century: 20, label: 'Siglo XX', selected: selectedCents.includes(20) }
    ];
  }

  loadArtists(): void {
    const nationalitySelected = this.nationalityFilters.filter(n => n.selected).map(n => n.name);
    const centuriesSelected = this.centuryFilters.filter(c => c.selected).map(c => c.century);
    const page = this.currentPage;
    const size = 9;

    this.artistsService.getArtists(
      this.search || undefined,
      this.sort || undefined,
      nationalitySelected.length ? nationalitySelected : undefined,
      centuriesSelected.length ? centuriesSelected : undefined,
      page,
      size
    ).subscribe({
      next: (data: any) => {
        this.artistsPage = data.content;
        this.artistsEmpty = data.content.length === 0;
        this.totalArtists = data.content.length;
        this.range = `${(page * size) + 1} - ${(page * size) + data.content.length}`;
        this.previousPage = this.currentPage > 1 ? this.currentPage - 1 : null;
        this.nextPage = data.content.length === size ? this.currentPage + 1 : null;
      },
      error: (err) => {
        this.error = 'Error al cargar los artistas';
        this.artistsPage = [];
        this.artistsEmpty = true;
      }
    });
  }

  onSearchSubmit() {
    this.currentPage = 1;
    this.navigateWithFilters();
  }

  onNationalityFilterChange() {
    this.currentPage = 1;
    this.navigateWithFilters();
  }

  onNationalityChange(nat: any) {
    nat.selected = !nat.selected;
    this.onNationalityFilterChange();
  }

  onCenturyFilterChange() {
    this.currentPage = 1;
    this.navigateWithFilters();
  }

  onCenturyChange(cent: any) {
    cent.selected = !cent.selected;
    this.onCenturyFilterChange();
  }

  onSortChange(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.sort = value;
    this.sortName = value === 'name';
    this.sortBirthDate = value === 'dateOfBirth';
    this.sortNationality = value === 'nationality';
    this.currentPage = 1;
    this.navigateWithFilters();
  }

  confirmDelete(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar este artista?')) {
      this.success = 'Artista eliminado correctamente (simulado)';
      this.loadArtists();
    }
  }

  getPageParams(page: number) {
    return {
      page,
      sort: this.sort,
      search: this.search || undefined,
      nationalityFilters: this.nationalityFilters.filter(n => n.selected).map(n => n.name),
      centuries: this.centuryFilters.filter(c => c.selected).map(c => c.century)
    };
  }

  private navigateWithFilters() {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: this.getPageParams(1),
      queryParamsHandling: 'merge'
    });
  }
}
