import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { MuseumsService } from 'src/app/api-client/api/museums.service';
import { MuseumRequestModel } from 'src/app/api-client/model/museumRequest';

@Component({
  selector: 'app-museums',
  templateUrl: './museums.component.html',
  styleUrls: ['./museums.component.css']
})
export class MuseumsComponent implements OnInit {
  error: string | null = null;
  success: string | null = null;

  sortName = false;
  sortFounded = false;
  sortLocation = false;
  sort: string = 'name';
  search: string = '';
  locationFiltersList: { name: string, selected: boolean }[] = [];

  museumsEmpty = false;
  museumsPage: MuseumRequestModel[] = [];
  range: string = '';
  totalMuseums: number = 0;
  currentPage: number = 1;
  previousPage: number | null = null;
  nextPage: number | null = null;

  admin = false;

  constructor(
    private museumsService: MuseumsService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.initFiltersFromParams(params);
      this.loadMuseums();
    });
  }

  initFiltersFromParams(params: any) {
    this.currentPage = +params['page'] || 1;
    this.search = params['search'] || '';
    const sort = params['sort'] || 'name';
    this.sort = sort;
    this.sortName = sort === 'name';
    this.sortFounded = sort === 'founded';
    this.sortLocation = sort === 'location';

    const locParam = params['locationFilters'];
    let selectedLocs: string[] = [];
    if (locParam) {
      if (Array.isArray(locParam)) {
        selectedLocs = locParam;
      } else {
        selectedLocs = [locParam];
      }
    }
    this.locationFiltersList = [
      { name: 'España', selected: selectedLocs.includes('España') },
      { name: 'Francia', selected: selectedLocs.includes('Francia') },
      { name: 'Italia', selected: selectedLocs.includes('Italia') }
    ];
  }

  loadMuseums(): void {
    const locationSelected = this.locationFiltersList.filter(l => l.selected).map(l => l.name);
    const page = this.currentPage;
    const size = 9;

    this.museumsService.getMuseumsPage(
      this.search || undefined,
      page,
      size,
      this.sort || undefined
    ).subscribe({
      next: (data: any) => {
        this.museumsPage = data.content;
        this.museumsEmpty = data.content.length === 0;
        this.totalMuseums = data.content.length;
        this.range = `${(page * size) + 1} - ${(page * size) + data.content.length}`;
        this.previousPage = this.currentPage > 1 ? this.currentPage - 1 : null;
        this.nextPage = data.content.length === size ? this.currentPage + 1 : null;
      },
      error: (err) => {
        this.error = 'Error al cargar los museos';
        this.museumsPage = [];
        this.museumsEmpty = true;
      }
    });
  }

  onSearchSubmit() {
    this.currentPage = 1;
    this.navigateWithFilters();
  }

  onLocationFilterChange() {
    this.currentPage = 1;
    this.navigateWithFilters();
  }

  onLocationChange(loc: any) {
    loc.selected = !loc.selected;
    this.onLocationFilterChange();
  }

  onSortChange(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.sort = value;
    this.sortName = value === 'name';
    this.sortFounded = value === 'founded';
    this.sortLocation = value === 'location';
    this.currentPage = 1;
    this.navigateWithFilters();
  }

  confirmDelete(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar este museo?')) {
      this.success = 'Museo eliminado correctamente (simulado)';
      this.loadMuseums();
    }
  }

  getPageParams(page: number): Params {
    return {
      page,
      sort: this.sort,
      search: this.search || undefined,
      locationFilters: this.locationFiltersList.filter(l => l.selected).map(l => l.name)
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
