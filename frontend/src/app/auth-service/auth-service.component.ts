import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../model/user.model';
import { Observable, catchError, map, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthServiceComponent {

  private username: string = '';
  private user: User | undefined;
  private isLogged: boolean;

  constructor(private http: HttpClient, private router: Router) {
    const storedUser = sessionStorage.getItem('user');
    if (storedUser) {
      this.user = JSON.parse(storedUser);
      this.username = this.user?.email as string;
      this.isLogged = true;
    } else {
      this.isLogged = false;
    }
  }

  reqIsLogged() {
    this.http.get('/api/v1/users/me', { withCredentials: true }).subscribe({
      next: (response: any) => {
        this.user = response as User;
        this.isLogged = true;
        sessionStorage.setItem('user', JSON.stringify(this.user));
        this.router.navigate(['/']);
      },
      error: (error: any) => {
        if (error.status != 404) {
          console.error('Error when asking if logged: ' + JSON.stringify(error));
        }
      }
    });
  }

  login(username: string, password: string) {
    this.username = username;
    const formData = { username: this.username, password: password };
    this.http.post("/api/v1/auth/login", formData, { withCredentials: true }).subscribe({
      next: (response: any) => { this.reqIsLogged() },
      error: (error: any) => { alert("Wrong credentials") }
    });
  }

  register(name: string, surname: string, email: string, password: string) {
    const formData = { name: name, surname: surname, email: email, password: password };
    this.http.post("/api/v1/signup", formData, { withCredentials: true }).subscribe({
      next: (response: any) => { this.router.navigate(['/login']) },
      error: (error: any) => { alert("Error") }
    });
  }

  logout() {
    const formData = { username: this.username };
    this.http.post("/api/v1/auth/logout", formData).subscribe({
      next: (response: any) => {
        console.log(response)
        this.user = undefined;
        this.isLogged = false;
        sessionStorage.removeItem('user');
        this.username = '';
        this.router.navigate(['/']);
      }
    })
  }

  forgotPassword(email: string) {
    const formData = { email: email };
    this.http.post("/api/v1/forgot-password", formData, { withCredentials: true }).subscribe(
      {
        next: (response: any) => { this.router.navigate(['/login']) },
        error: (error: any) => { alert("Error") }
      });
  }

  updateUserLogged() {
    this.http.get('/api/v1/users/me', { withCredentials: true }).subscribe(
      {
        next: (response: any) => {
          this.user = response as User;
          sessionStorage.setItem('user', JSON.stringify(this.user));
        },
        error: (error: any) => { alert("Error") }
      });
  }

  userReviewList(): Observable<any | null> {
    return this.http.get<any[]>('/api/v1/users/me/my-reviews-list', { withCredentials: true }).pipe(
      catchError(error => {
        if (error.status !== 404) {
          console.error('Error when asking if logged: ' + JSON.stringify(error));
        }
        return of(null);
      })
    );
  }

  userCourseList(): Observable<any | null> {
    return this.http.get<any[]>('/api/v1/users/me/my-course-list', { withCredentials: true }).pipe(
      catchError(error => {
        if (error.status !== 404) {
          console.error('Error when asking if logged: ' + JSON.stringify(error));
        }
        return of(null);
      })
    );
  }

  deleteUser() {
    this.http.delete("/api/v1/users/me", { withCredentials: true }).subscribe();
    this.logout();
  }

  updatePassword(currentpassword: string, pass1: string, pass2: string) {
    console.log({ currentpassword, pass1, pass2 });
    this.http.put('/api/v1/users/me/password', { "currentpassword": currentpassword, "pass1": pass1, "pass2": pass2 }, { withCredentials: true })
      .subscribe(
        response => {
          console.log('Password updated successfully', response);
        },
        error => {
          console.error('Error updating password', error);
        }
      );
  }

  isCoursePartOfUserCourses(courseId: number) {
    console.log("isCoursePartOfUserCourses de AuthServiceComponent")
    return this.http.get("/api/v1/users/me/courses/" + courseId, { withCredentials: true })
      .pipe(
        map((response: any) => {
          console.log(response)
          return response && response === true;
        })
      );
  }

  currentUser(): User | undefined {
    return this.user;
  }

  getUsername(): string {
    return this.username;
  }

  getUserName(): string | undefined {
    return this.user?.name;
  }

  getUserEmail(): string | undefined {
    return this.user?.email;
  }

  getUserSurname(): string | undefined {
    return this.user?.surname;
  }

  isAdmin(): boolean {
    return !!(this.user && this.user.roles && this.user.roles.indexOf('ADMIN') !== -1);
  }

  isLoggedIn(): boolean {
    return this.isLogged;
  }
}