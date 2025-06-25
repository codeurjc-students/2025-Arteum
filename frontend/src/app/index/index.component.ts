import { Component, OnInit } from '@angular/core';
import { ArtworksService } from 'src/app/api-client/api/artworks.service';
import { ArtworkRequestModel } from 'src/app/api-client/model/artworkRequest';
import { UsersService } from 'src/app/api-client/api/users.service';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css'],
})
export class IndexComponent implements OnInit {
confirmDelete(arg0: number|undefined) {
throw new Error('Method not implemented.');
}
  top7Artworks: ArtworkRequestModel[] = [];
  logged = false;
  admin = false;

  constructor(
    private artworksService: ArtworksService,
    private usersService: UsersService
  ) {}

  ngOnInit(): void {
    this.loadArtworks();
  }

  loadArtworks(): void {
    this.artworksService.getTopRatedArtworks().subscribe({
      next: (data: ArtworkRequestModel[]) => (this.top7Artworks = data),
      error: (err: any) => console.error('Error loading artworks:', err),
    });
  }

}
