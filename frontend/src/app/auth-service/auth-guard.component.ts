import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthServiceComponent } from './auth-service.component';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard  {

    constructor(private authService: AuthServiceComponent, private router: Router) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        const isAdminRequired = route.data['isAdmin'];
        if (this.authService.isLoggedIn()) {
            if (isAdminRequired && this.authService.isAdmin()) {
                return true;
            } else if (!isAdminRequired) {
                return true;
            } else {
                this.router.navigate(['/']);
                return false;
            }
        } else {
            this.router.navigate(['/login']);
            return false;
        }
    }
}
