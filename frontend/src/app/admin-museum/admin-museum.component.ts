import { Component } from '@angular/core';
import { AdminRestControllerService } from '../api-client/api/adminRestController.service';

@Component({
  selector: 'app-admin-museum',
  templateUrl: './admin-museum.component.html',
  styleUrls: ['./admin-museum.component.css']
})
export class AdminMuseumComponent {
  error: string | null = null;
  success: string | null = null;
  token: string = '';
  museum: any = {
    name: '',
    location: '',
    founded: null,
    description: '',
    image: null
  };

  constructor(private adminService: AdminRestControllerService) {}

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.museum.image = input.files[0];
    }
  }

  onSubmit() {
    this.error = null;
    this.success = null;
    if (
      !this.museum.name ||
      !this.museum.location ||
      !this.museum.founded ||
      !this.museum.description ||
      !this.museum.image
    ) {
      this.error = 'Todos los campos obligatorios deben estar completos.';
      return;
    }
    this.adminService.createMuseum(
      this.museum.name,
      this.museum.location,
      this.museum.founded,
      this.museum.description,
      this.museum.image
    ).subscribe({
      next: () => {
        this.success = 'Museo creado correctamente.';
        this.museum = {
          name: '',
          location: '',
          founded: null,
          description: '',
          image: null
        };
      },
      error: () => {
        this.error = 'Error al crear el museo.';
      }
    });
  }
}
