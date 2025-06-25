import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'
  ]
})

export class FooterComponent {
  constructor(private http: HttpClient) { }
}
