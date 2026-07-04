import { Component, OnInit } from '@angular/core';
import { NgIf, NgClass } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Authservice } from '../service/authservice';
import { HttpClientModule } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [NgIf, NgClass, FormsModule, HttpClientModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.scss'],
  providers: [Authservice],
})
export class Navbar implements OnInit{
  isLoggedIn = false;
  ngOnInit():void{
  this.isLoggedIn = this.authService.getDataFromToken(localStorage.getItem('token'))?.userId==null ? false : true;
  }

  showModal = false;
  isLoginMode = true;
  searchQuery: string = '';

  showCreatePostModal = false;
  postDescription = '';
  postImage: File | null = null;

  // Form fields
  username = '';
  fullname = '';
  phone: number | null = null;
  email = '';
  dob = '';
  password = '';
  confirmPassword = '';

  notificationMessage = '';
  notificationType: 'success' | 'error' | '' = '';
  notificationTimeoutId: any;

  constructor(private authService: Authservice, private router: Router) {}

  openModal() {
    this.showModal = true;
    this.clearNotification();
  }

  closeModal() {
    this.showModal = false;
  }

  toggleMode(event: Event) {
    event.preventDefault();
    this.isLoginMode = !this.isLoginMode;

    // Clear form fields
    this.username = '';
    this.fullname = '';
    this.phone = null;
    this.email = '';
    this.dob = '';
    this.password = '';
    this.confirmPassword = '';
    this.clearNotification();
  }

  submitAuthForm(form: NgForm) {
    if (!form.valid) {
      this.setNotification('Please enter all fields correctly.', 'error');
      return;
    }

    if (!this.isLoginMode && this.password !== this.confirmPassword) {
      this.setNotification('Passwords do not match.', 'error');
      return;
    }

    if (this.isLoginMode) {
      this.authService.login(this.email, this.password).subscribe({
        next: (res) => {
          if (res.success) {
            localStorage.setItem('token', res.token);
            const role = this.authService.getDataFromToken(res.token).role;
            localStorage.setItem('role', this.authService.getDataFromToken(res.token).role);
            this.setNotification(res.message, 'success');
            this.closeModal();
            this.ngOnInit();
            if(role === 'Admin')  this.router.navigate(['/admin/home'])
            else this.router.navigate(['/home']);
          } else {
            this.setNotification(res.message, 'error');
          }
        },
        error: (err) => {
          this.setNotification(err.error?.message || 'Login failed', 'error');
        },
      });
    } else {
      this.authService
        .signup(this.username, this.email, this.password, this.fullname, this.phone, this.dob)
        .subscribe({
          next: (res) => {
            if (res.success) {
              localStorage.setItem('token', res.token);
              this.setNotification(res.message, 'success');
              this.closeModal();
            } else {
              this.setNotification(res.message, 'error');
            }
          },
          error: (err) => {
            this.setNotification(err.error?.message || 'Signup failed', 'error');
          },
        });
    }
  }

  logout() {
    this.isLoggedIn = false;
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.setNotification('Logged out successfully!', 'success');
    this.router.navigate(['/']);
  }

  setNotification(message: string, type: 'success' | 'error') {
    this.notificationMessage = message;
    this.notificationType = type;

    if (this.notificationTimeoutId) {
      clearTimeout(this.notificationTimeoutId);
    }

    this.notificationTimeoutId = setTimeout(() => {
      this.clearNotification();
    }, 4000);
  }

  clearNotification() {
    this.notificationMessage = '';
    this.notificationType = '';
    if (this.notificationTimeoutId) {
      clearTimeout(this.notificationTimeoutId);
      this.notificationTimeoutId = null;
    }
  }

  onSearch() {
    if (this.searchQuery.trim()) {
      this.router.navigate(['/search'], {
        queryParams: { username: this.searchQuery.trim() },
      });
    }
  }

  openCreatePostModal() {
    this.showCreatePostModal = true;
  }

  closeCreatePostModal() {
    this.showCreatePostModal = false;
    this.postDescription = '';
    this.postImage = null;
  }

  submitPost() {
    this.authService.create(this.postDescription, this.postImage).subscribe({
        next: (res) => {
          if (res.success) {
            this.setNotification(res.message, 'success');
            this.closeCreatePostModal()
          } else {
            this.setNotification(res.message, 'error');
          }
        },
        error: (err) => {
          this.setNotification(err.error?.message,'error');
        },
      });
  }

  onImageSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.postImage = file;
    }
  }

  goToHome() {
  this.router.navigate(['/']);
}

}
