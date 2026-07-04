import { Component, OnInit } from '@angular/core';
import { NgIf, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Authservice } from '../service/authservice';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NgIf, CommonModule, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  constructor(private authService: Authservice, private router: Router) {}

  posts: any = [];
  Math = Math;

  notificationMessage = '';
  notificationType: 'success' | 'error' | '' = '';
  notificationTimeoutId: any;

  showLikedUsersModal: boolean = false;
  likedUsers: any[] = [];
  likedUsersPage: number = 0;
  likedUsersTotalPages: number = 0;
  likedUsersPostId: number | null = null;

  newComment: any;
  showCommentsModal = false;
  currentComments: any[] = [];
  commentUsersPage: number = 0;
  commentUsersTotalPages: number = 0;
  commentUsersPostId: number | null = null;

  currentUserId: any = null;

  ngOnInit() {
    this.currentUserId = Number(
      this.authService.getDataFromToken(localStorage.getItem('token')).userId
    );
    this.authService.getPosts().subscribe({
      next: (data) => {
        this.posts = [...data];
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  goToProfile(userId: string) {
    this.router.navigate(['/profile'], { queryParams: { id: userId } });
  }

  likePost(id: any) {
    this.authService.like(id).subscribe({
      next: (data) => {
        this.ngOnInit();
        this.setNotification(data.message, 'success');
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  RemoveLike(id: any) {
    this.authService.removeLike(id).subscribe({
      next: (data) => {
        this.ngOnInit();
        this.setNotification(data.message, 'success');
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  GetUsers(postId: number, likes: number) {
    if (likes > 0) {
      this.likedUsersPage = 0;
      this.likedUsersPostId = postId;
      this.loadLikedUsersPage();
    }
  }

  loadLikedUsersPage() {
    this.showCommentsModal = false;
    if (this.likedUsersPostId === null) return;
    this.authService.getLikedUsers(this.likedUsersPostId, this.likedUsersPage, 3).subscribe({
      next: (data) => {
        this.authService.getUsersByIds(data.content).subscribe({
          next: (res) => {
            this.showLikedUsersModal = true;
            this.likedUsers = [...res];
            this.likedUsersTotalPages = data.totalPages;
          },
          error: (err) => this.setNotification(err.error.message, 'error'),
        });
      },
      error: (err) => this.setNotification(err.error.message, 'error'),
    });
  }

  prevLikedUsersPage() {
    if (this.likedUsersPage > 0) {
      this.likedUsersPage--;
      this.loadLikedUsersPage();
    }
  }

  nextLikedUsersPage() {
    if (this.likedUsersPage + 1 < this.likedUsersTotalPages) {
      this.likedUsersPage++;
      this.loadLikedUsersPage();
    }
  }

  commentOnPost(id: any) {
    this.authService.comment(id, this.newComment).subscribe({
      next: (data) => {
        this.ngOnInit();
        this.setNotification(data.message, 'success');
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  GetCommentUsers(id: any, comments: any) {
    if (comments > 0) {
      this.commentUsersPage = 0;
      this.commentUsersPostId = id;
      this.loadCommentedUsersPage();
    }
  }

  loadCommentedUsersPage() {
    this.showLikedUsersModal = false;
    this.authService
      .getCommentedUsers(this.commentUsersPostId, this.commentUsersPage, 5)
      .subscribe({
        next: (data) => {
          this.currentComments = [...data.content];
          this.showCommentsModal = true;
          this.commentUsersTotalPages = data.totalPages;
        },
        error: (err) => {
          this.setNotification(err.error.message, 'error');
        },
      });
  }


  prevCommentsPage() {
    if (this.commentUsersPage >0){
      this.commentUsersPage--;
      this.loadCommentedUsersPage();
    }
  }

  nextCommentsPage() {
     if (this.commentUsersPage + 1 < this.commentUsersTotalPages) {
     this.commentUsersPage++;
      this.loadCommentedUsersPage();
    }
  }

  deleteComment(commentId: any) {
    this.authService.removeComment(commentId).subscribe({
      next: (data) => {
        this.setNotification(data.message, 'success');
        this.showCommentsModal = false;
        this.ngOnInit();
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  closeLikedUsersModal() {
    this.showLikedUsersModal = false;
    this.likedUsers = [];
  }

  closeCommentsModal() {
    this.showCommentsModal = false;
    this.currentComments = [];
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
}
