import { Component, OnInit } from '@angular/core';
import { UsersService } from 'src/app/api-client/api/users.service';
import { Router, ActivatedRoute, Params } from '@angular/router';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  error: string | null = null;
  success: string | null = null;
  search: string = '';
  range: string = '';
  totalUsers: number = 0;
  currentPage: number = 1;
  name: string = '';
  id: number | null = null;
  sort: string = 'createdAt';
  previousPage: number | null = null;
  nextPage: number | null = null;
  sortName: boolean = false;
  sortFounded: boolean = false;
  sortLocation: boolean = false;
  sortCreatedAt: boolean = true;
  sortNameAsc: boolean = false;
  usersEmpty: boolean = false;
  usersPage: any[] = [];
  admin: boolean = false;

  constructor(
    private usersService: UsersService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.currentPage = +params['page'] || 1;
      this.search = params['search'] || '';
      this.sort = params['sort'] || 'createdAt';
      this.sortCreatedAt = this.sort === 'createdAt';
      this.sortName = this.sort === 'nameDesc';
      this.sortNameAsc = this.sort === 'nameAsc';
      this.loadUsers();
    });
  }

  loadUsers(): void {
    const page = this.currentPage;
    const size = 12;
    this.usersService.getUsers(page, size, this.search || undefined, this.sort || undefined).subscribe({
      next: (data: any) => {
        this.usersPage = data.content;
        this.usersEmpty = data.content.length === 0;
        this.totalUsers = data.length;
        this.range = data.length > 0 ? `${page * size + 1} - ${page * size + data.content.length}` : '0';
        this.previousPage = this.currentPage > 1 ? this.currentPage - 1 : null;
        this.nextPage = data.content.length === size ? this.currentPage + 1 : null;
      },
      error: (err) => {
        this.error = 'Error al cargar los usuarios';
        this.usersPage = [];
        this.usersEmpty = true;
      }
    });
  }

  onSearchSubmit() {
    this.currentPage = 1;
    this.navigateWithFilters();
  }

  onSortChange(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.sort = value;
    this.sortCreatedAt = value === 'createdAt';
    this.sortName = value === 'nameDesc';
    this.sortNameAsc = value === 'nameAsc';
    this.currentPage = 1;
    this.navigateWithFilters();
  }

  confirmDelete(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar este usuario?')) {
      this.success = 'Usuario eliminado correctamente (simulado)';
      this.loadUsers();
    }
  }

  getPageParams(page: number): Params {
    return {
      page,
      sort: this.sort,
      search: this.search || undefined
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
