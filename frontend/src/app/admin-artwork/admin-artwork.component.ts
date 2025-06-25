import { Component } from '@angular/core';
import { AdminRestControllerService } from '../api-client/api/adminRestController.service';

@Component({
  selector: 'app-admin-artwork',
  templateUrl: './admin-artwork.component.html',
  styleUrls: ['./admin-artwork.component.css']
})
export class AdminArtworkComponent {
  error: string | null = null;
  success: string | null = null;
  artwork: any = {
    title: '',
    creationYear: null,
    description: '',
    artistId: null,
    museumId: null,
    image: null
  };

  constructor(private adminService: AdminRestControllerService) {}

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.artwork.image = input.files[0];
    }
  }

  onSubmit() {
    this.error = null;
    this.success = null;
    if (
      !this.artwork.title ||
      !this.artwork.creationYear ||
      !this.artwork.description ||
      !this.artwork.artistId ||
      !this.artwork.museumId ||
      !this.artwork.image
    ) {
      this.error = 'Todos los campos son obligatorios.';
      return;
    }
    this.adminService.createArtwork(
      this.artwork.title,
      this.artwork.creationYear,
      this.artwork.description,
      this.artwork.artistId,
      this.artwork.museumId,
      this.artwork.image
    ).subscribe({
      next: () => {
        this.success = 'Obra creada correctamente.';
        this.artwork = {
          title: '',
          creationYear: null,
          description: '',
          artistId: null,
          museumId: null,
          image: null
        };
      },
      error: (err) => {
        this.error = 'Error al crear la obra.';
      }
    });
  }
}
