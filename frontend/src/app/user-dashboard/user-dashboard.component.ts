import { Component, OnInit } from '@angular/core';
import { UsersService } from 'src/app/api-client/api/users.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthServiceComponent } from '../auth-service/auth-service.component';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit {
  errorMessage: string | null = null;
  success: string | null = null;
  admin: boolean = false;
  name: string = '';
  token: string = '';
  created: string = '';
  email: string = '';
  hasimage: boolean = false;
  location: string = '';
  biography: string = '';

  constructor(
    private usersService: UsersService,
    private http: HttpClient,
    private router: Router,
    public authService: AuthServiceComponent,
  ) {}

  ngOnInit(): void {
    this.loadUserMe();
  }

  loadUserMe(): void {
    this.http.get<any>('/api/v1/users/me').subscribe({
      next: (user) => {
        this.name = user.name || '';
        this.email = user.email || '';
        this.created = user.created || '';
        this.admin = !!user.admin;
        this.location = user.location || '';
        this.biography = user.biography || '';
        this.hasimage = !!user.hasimage;
      },
      error: (err) => {
        this.errorMessage = 'No se pudo cargar la información del usuario.';
      }
    });
  }

  confirmDeleteAccount() {
    if (confirm('¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no tiene vuelta atrás, se borrarán todos tus datos.')) {
      this.usersService.deleteMyAccount().subscribe({
        next: () => {
          this.success = 'Cuenta eliminada correctamente.';
          this.router.navigate(['/']);
        },
        error: () => {
          this.errorMessage = 'Error al eliminar la cuenta.';
        }
      });
    }
  }

  logout() {
    this.authService.logout();
  }
}
