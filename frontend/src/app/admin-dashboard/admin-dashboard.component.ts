import { Component } from '@angular/core';
import { AuthServiceComponent } from '../auth-service/auth-service.component';
import { HttpClient } from '@angular/common/http';
import { User } from '../model/user.model';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent {
  error: any;
  success: any;
}
