import { Component } from '@angular/core';
import { AuthServiceComponent } from '../auth-service/auth-service.component';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})

export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessage: any;
  success: any;

  constructor(public authService: AuthServiceComponent) { }

  login() {
    if (this.username !== '' && this.password !== '') {
      this.authService.login(this.username, this.password);
    }
  }
}