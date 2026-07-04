import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Authservice } from '../../service/authservice';
import { FormsModule } from '@angular/forms';
import { NgIf, CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-users',
  imports:[FormsModule, CommonModule],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.scss'
})
export class AdminUsers implements OnInit {

  userDetails: any[] = [];
  Math = Math;
  currentPage: number = 0;
  pageSize: number = 6;
  totalUsers: number = 0;
  totalPages: number = 0;

  sortBy: string = 'username';
  sortDir: string = 'asc';

  constructor(private router: Router, private authService: Authservice) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    this.authService.getUsers(this.currentPage, this.pageSize, this.sortBy, this.sortDir)
      .subscribe({
        next: (data) => {
          this.userDetails = data.content;
          this.totalUsers = data.totalElements;
          this.totalPages = Math.ceil(this.totalUsers / this.pageSize);
        },
        error: (err) => console.error(err)
      });
  }

  viewAccount(id: any) {
    this.router.navigate(['/profile'], { queryParams: { id } });
  }

  deleteUser(id: any) {
    this.authService.deleteUser(id).subscribe({
      next: () => this.loadUsers(),
      error: (err) => console.error(err)
    });
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadUsers();
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadUsers();
    }
  }

  onSortChange() {
    this.currentPage = 0; 
    this.loadUsers();
  }
}