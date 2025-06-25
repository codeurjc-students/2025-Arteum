import { Component } from '@angular/core';
import { AuthServiceComponent } from '../auth-service/auth-service.component';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'
  ]
})
export class HeaderComponent {
  userImage: SafeUrl | undefined = "/assets/img/user/default-user-icon.jpg";

  logged: boolean = false;
  admin: boolean = false;
  userName: string = '';
  header01: boolean = true;
  header02: boolean = false;
  header03: boolean = false;
  header04: boolean = false;
  header05: boolean = false;
  header06: boolean = false;
  header07: boolean = false;
  headerAdmin: boolean = false;

  constructor(
    private sanitizer: DomSanitizer,
    public authService: AuthServiceComponent,
    private router: Router
  ) {
    this.logged = this.authService.isLoggedIn();
    this.admin = this.authService.isAdmin() ?? false;
    this.userName = this.authService.getUserName?.() || this.authService.getUsername?.() || '';

    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateHeaderActive(event.urlAfterRedirects);
      }
    });
    this.updateHeaderActive(this.router.url);
  }

  updateHeaderActive(url: string) {
    this.header01 = this.header02 = this.header03 = this.header04 = this.header05 = this.header06 = this.header07 = this.headerAdmin = false;

    if (url === '/' || url.startsWith('/inicio')) {
      this.header01 = true;
    } else if (url.startsWith('/artworks')) {
      this.header02 = true;
    } else if (url.startsWith('/artists')) {
      this.header03 = true;
    } else if (url.startsWith('/museums')) {
      this.header05 = true;
    } else if (url.startsWith('/users')) {
      this.header06 = true;
    } else if (url.startsWith('/user/recommended-artworks')) {
      this.header07 = true;
    } else if (url.startsWith('/admin')) {
      this.headerAdmin = true;
    } else if (url.startsWith('/contact') || url.startsWith('/about') || url.startsWith('/faq')) {
      this.header04 = true;
    }
  }

  isAdmin(): boolean | undefined {
    return this.authService.isAdmin()
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn()
  }

  getUserEmail(): String {
    return this.authService.getUsername();
  }

  currentUserName(): String | undefined {
    return this.authService.getUserName();
  }

  logout() {
    this.authService.logout();
  }
}
