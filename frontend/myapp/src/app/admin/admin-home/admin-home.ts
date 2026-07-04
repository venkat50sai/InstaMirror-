import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Authservice } from '../../service/authservice';

@Component({
  selector: 'app-admin-home',
  imports: [],
  templateUrl: './admin-home.html',
  styleUrl: './admin-home.scss'
})
export class AdminHome implements OnInit{
  totalUsers = 0;
  totalPosts = 0;
  totalProducts = 0;

  constructor(private router : Router, private authService : Authservice){}

  ngOnInit(): void {
  this.authService.getUserCount().subscribe({
    next: (data) => this.totalUsers = data.count,
    error: (err) => {
      // this.setNotification(err.error.message, 'error');
      this.totalUsers = 0;
    }
  });

  this.authService.getPostsCount().subscribe({
    next: (data) => this.totalPosts = data.count,
    error: (err) => {
      // this.setNotification(err.error.message, 'error');
      this.totalPosts = 0;
    }
  });

  this.authService.getProductCount().subscribe({
    next: (data) => this.totalProducts = data.count,
    error: (err) => {
      // this.setNotification(err.error.message, 'error');
      this.totalProducts = 0;
    }
  });


}

  manageUsers(){
    this.router.navigate(['/admin/users'])
  }

  managePosts(){
    this.router.navigate(['/admin/posts'])
  }

  manageProducts(){
     this.router.navigate(['/admin/products'])
  }
}
