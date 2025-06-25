import { Component } from '@angular/core';
import { AuthServiceComponent } from '../auth-service/auth-service.component';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  name: string = '';
  surname: string = '';
  email: string = '';
  password: string = '';
  success: any;
  errorMessage: any;

  constructor(public authService: AuthServiceComponent) { }

  register() {
    if (this.name !== '' && this.surname !== '' && this.email !== '' && this.password !== '') {
      this.authService.register(this.name, this.surname, this.email!, this.password);
    } else {
      alert("Faltan campos por rellenar")
    }
  }
}
