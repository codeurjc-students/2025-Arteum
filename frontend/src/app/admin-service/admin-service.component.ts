import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { observableToBeFn } from 'rxjs/internal/testing/TestScheduler';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = '/api/courses';

  constructor(private http: HttpClient) { }

  createCourse(Sname: String, Scategory: String, Sdescription: String): Observable<any> {
    const name = Sname
    const category = Scategory
    const description = Sdescription
    const formData = { name, category, description }
    return this.http.post(this.apiUrl, formData);
  }

  deleteCourse(id: String) {
    return this.http.delete(this.apiUrl+"/"+id)
  }

  uploadFile(file: File, id: number): Observable<any> {
    let pdfDoc = new FormData()
    pdfDoc.append("pdfDoc", file)
    return this.http.post(this.apiUrl + "/" + id + "/files", pdfDoc)
  }

  uploadImage(image: any, id: number): Observable<any> {
    const newformData = new FormData()
    newformData.append("image", image)
    return this.http.post(this.apiUrl + "/" + id + "/photos", newformData)
  }

  chargeUserImage(user: number): Observable<any> {
    return this.http.get('/api/admins/users/' + user + '/image', { responseType: 'blob' })
  }

  chargeUserInfo(user: number): Observable<any> {
    return this.http.get('/api/admins/users/' + user)
  }

  uploadNewUserInfo(userId: number, userName: string, userSurname: string,) {
    console.log("reached!")
    const name = userName
    const surname = userSurname
    const newUser = { name, surname }
    return this.http.put("/api/admins/users/" + userId, newUser)
  }

  uploadUserImage(userId: number, image: any): Observable<any> {
    const formData = new FormData();
    console.log("executed!")
    formData.append("image", image);
    return this.http.put("/api/admins/users/" + userId + "/photos", formData)
  }

  changeCourse(sName: string, sCategory: string, sDescription: string, id: number): Observable<any> {
    const name = sName;
    const category = sCategory;
    const description = sDescription;
    const formData = { name, category, description }
    return this.http.put(this.apiUrl + "/" + id, formData)
  }

  getUsers(page: number): Observable<any> {
    return this.http.get("/api/admins/users?page=" + page)
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete("/api/admins/users/" + id)
  }

  deleteUserPhoto(id: number) {
    return this.http.delete("/api/admins/users/" + id + "/photos")
  }
}
