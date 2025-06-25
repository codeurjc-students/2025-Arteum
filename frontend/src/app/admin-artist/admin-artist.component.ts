import { Component } from '@angular/core';
import { AdminRestControllerService } from '../api-client/api/adminRestController.service';

@Component({
  selector: 'app-admin-artist',
  templateUrl: './admin-artist.component.html',
  styleUrls: ['./admin-artist.component.css']
})
export class AdminArtistComponent {
  error: string | null = null;
  success: string | null = null;
  token: string = '';
  artist: any = {
    name: '',
    nationality: '',
    dateOfBirth: '',
    dateOfDeath: '',
    biography: '',
    image: null
  };

  constructor(private adminService: AdminRestControllerService) {}

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.artist.image = input.files[0];
    }
  }

  onSubmit() {
    this.error = null;
    this.success = null;
    if (
      !this.artist.name ||
      !this.artist.nationality ||
      !this.artist.dateOfBirth ||
      !this.artist.biography ||
      !this.artist.image
    ) {
      this.error = 'Todos los campos obligatorios deben estar completos.';
      return;
    }
    this.adminService.createArtist(
      this.artist.name,
      this.artist.nationality,
      this.artist.dateOfBirth,
      this.artist.biography,
      this.artist.image,
      this.artist.dateOfDeath ? this.artist.dateOfDeath : undefined
    ).subscribe({
      next: () => {
        this.success = 'Artista creado correctamente.';
        this.artist = {
          name: '',
          nationality: '',
          dateOfBirth: '',
          dateOfDeath: '',
          biography: '',
          image: null
        };
      },
      error: () => {
        this.error = 'Error al crear el artista.';
      }
    });
  }
}
