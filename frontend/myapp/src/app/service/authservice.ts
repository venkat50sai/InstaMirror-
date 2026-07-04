import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Authservice {
  private gatewayApiUrl = (window as any).GATEWAY_API_URL || 'http://localhost:8888';

  constructor(private http: HttpClient) {}

  getDataFromToken(token?: any): any{
  token = token || localStorage.getItem('token') || '';
  if (!token) return null;

  try {
    const payload = token.split('.')[1];
    const decodedPayload = JSON.parse(atob(payload));
    return decodedPayload;
  } catch (error) {
    console.error('Invalid token:', error);
    return null;
  }
}

  login(email: any, password: any): Observable<any> {
  return this.http.post<any>(`${this.gatewayApiUrl}/user/login`, { email, password })
    .pipe(
      tap(res => {
        if (res.token) {
          localStorage.setItem('token', res.token);
          localStorage.setItem('role', "USER");
        }
      })
    );
}


  signup(
    username: any,
    email: any,
    password: any,
    fullname: any,
    phone: any,
    dob: any
  ): Observable<any> {
    const id: any = this.http.post<any>(`${this.gatewayApiUrl}/user/register`, {
      fullname,
      phone,
      username,
      email,
      password,
      dob,
    });

    localStorage.setItem('id', id);
    return id;
  }

  getUserById(id: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/user/getById?id=${id}`, { headers });
 }

  updateUser(id: any, data: any, imageFile?: any): Observable<any> {
    const formData = new FormData();
    for (const key in data) {
      if (data.hasOwnProperty(key)) {
        formData.append(key, data[key]);
      }
    }
    if (imageFile) {
      formData.append('img', imageFile);
    }
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.put<any>(`${this.gatewayApiUrl}/user/updateUser`, formData, {headers});
  }

  followers(id: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/friend/followers?id=${id}`, {headers});
  }

  followings(id: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/friend/followings?id=${id}`, {headers});
  }

  getUsersByIds(userIds: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.post<any>(`${this.gatewayApiUrl}/user/batch`, userIds, {headers});
  }

  getUserCount(): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/user/user-Count`, {headers});
  }

  getUsers(page: number = 0, size: number = 3,sortBy: string = 'username', sortDir: string = 'asc' ): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/user/all?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`, {headers});
  }

  deleteUser(id: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.delete<any>(`${this.gatewayApiUrl}/user/delete?id=${id}`, {headers});
  }

  follow(userid: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.post<any>(
      `${this.gatewayApiUrl}/friend/follow?id=${userid}`,
      null,
      {headers}
    );
  }


  unfollow(userid: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.delete<any>(
      `${this.gatewayApiUrl}/friend/unfollow?id=${userid}`,
      {headers}
    );
  }

  check(userid: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(
      `${this.gatewayApiUrl}/friend/check?id=${userid}`,
      {headers}
    );
  }

  search(username: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/user/search?username=${username}`, {headers});
  }

  create(caption: any, img: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    const formData = new FormData();
    const id: any = this.getDataFromToken(localStorage.getItem('token')).userId;
    formData.append('userId', id);
    if (img) {
      formData.append('image', img);
    }
    formData.append('caption', caption);
    return this.http.post<any>(`${this.gatewayApiUrl}/post/create`, formData, {headers});
  }

  getPosts(): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/post/getAllPosts`, {headers});
  }

  getUserPosts(id: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/post/getUserPost?id=${id}`, {headers});
  }

   getPostsCount(): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/post/post-Count`, {headers});
  }

  delete(id: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.delete<any>(`${this.gatewayApiUrl}/post/delete?id=${id}`, {headers});
  }

  like(postId:any):Observable<any>{
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/like/give?id=${postId}`, {headers});
  }

  removeLike(postId:any):Observable<any>{
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.delete<any>(`${this.gatewayApiUrl}/like/unlike?id=${postId}`, {headers});
  }

  deleteLike(id:any, postId:any):Observable<any>{
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.delete<any>(`${this.gatewayApiUrl}/like/removeLike?id=${id}&postId=${postId}`, {headers});
  }

    getLikedUsers(postId:any, page: number = 0, size: number = 3):Observable<any>{
      const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
      return this.http.get<any>(`${this.gatewayApiUrl}/like/likedUsers?id=${postId}&page=${page}&size=${size}`, {headers});
    }

  comment(postId:any, comment:any):Observable<any>{
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/comment/give/${postId}?comment=${encodeURIComponent(comment)}`, {headers});
  }

  removeComment(id:any):Observable<any>{
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.delete<any>(`${this.gatewayApiUrl}/comment/delete?id=${id}`, {headers});
  }

   getCommentedUsers(postId:any, page: number = 0, size: number = 5):Observable<any>{
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/comment/commentedUsers?id=${postId}&page=${page}&size=${size}`, {headers});
  }

  download(id: any, format:any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/user/download?id=${id}&format=${format}`, { headers, responseType: 'blob' as 'json' });
  }

  getProducts(): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/product/getAllProducts`, { headers});
  }

  getProductById(id:any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/product/products?id=${id}`, { headers});
  }

  updateProduct(id: any, product:any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.put<any>(`${this.gatewayApiUrl}/product/updateProduct?id=${id}`,product ,{headers});
  }

  batchProduct(ids:any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.post<any>(`${this.gatewayApiUrl}/product/batch}`,ids ,{headers});
  }

  deleteProduct(id: any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.delete<any>(`${this.gatewayApiUrl}/product/delete?id=${id}`,{headers});
  }

  addProducts(product:any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.post<any>(`${this.gatewayApiUrl}/product/addProduct`, product, { headers});
  }

  getProductCount(): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/product/count`,{ headers});
  }

  addToCart(quantity:any, productId:any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.post<any>(`${this.gatewayApiUrl}/cart/addCart`,{quantity, productId},{headers});
  }

  updateCart(quantity:any, productId:any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.put<any>(`${this.gatewayApiUrl}/cart/updateCart`,{quantity, productId}, {headers});
  }

  getCartCount(): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/cart/count`, { headers});
  }

  getCart(): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.get<any>(`${this.gatewayApiUrl}/cart/getCart`, { headers});
  }

  removeItem(id:any): Observable<any> {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };
    return this.http.delete<any>(`${this.gatewayApiUrl}/cart/removeItem?productId=${id}`, {headers});
  }

}
