import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Authservice } from '../../service/authservice';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-posts',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-posts.html',
  styleUrls: ['./admin-posts.scss']
})
export class AdminPosts implements OnInit {

  posts: any[] = [];

  // Likes
  likedUsers: any[] = [];
  likedUsersPage = 0;
  likedUsersTotalPages = 0;
  likedUsersPostId: number | null = null;
  showLikedUsersModal = false;

  // Comments
  currentComments: any[] = [];
  commentUsersPage = 0;
  commentUsersTotalPages = 0;
  commentUsersPostId: number | null = null;
  showCommentsModal = false;

  constructor(private router: Router, private authService: Authservice) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts() {
    this.authService.getPosts().subscribe({
      next: (data) => this.posts = [...data],
      error: (err) => console.error(err)
    });
  }

  deletePost(postId: number) {
    // this.authService.delete(postId).subscribe({
    //   next: () => this.loadPosts()
    // });
  }

  GetUsers(postId: number, likes: number) {
    if (likes > 0) {
      this.likedUsersPage = 0;
      this.likedUsersPostId = postId;
      this.loadLikedUsersPage();
    }
  }

  loadLikedUsersPage() {
    if (!this.likedUsersPostId) return;

    this.showCommentsModal = false;

    this.authService.getLikedUsers(this.likedUsersPostId, this.likedUsersPage, 5).subscribe({
      next: (data) => {
        this.authService.getUsersByIds(data.content).subscribe({
          next: (res) => {
            this.likedUsers = res;
            this.likedUsersTotalPages = data.totalPages;
            this.showLikedUsersModal = true;
          }
        });
      }
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

  removeLike(id : any){
    // this.authService.deleteLike(id, this.likedUsersPostId).subscribe({
    //   next: (data) => {
    //     this.ngOnInit();
    //   },
    //   error: (err) => {
    //     console.log(err);
    //   },
    // });
  }

  
  GetCommentUsers(postId: number, comments: number) {
    if (comments > 0) {
      this.commentUsersPage = 0;
      this.commentUsersPostId = postId;
      this.loadCommentedUsersPage();
    }
  }

  loadCommentedUsersPage() {
    if (!this.commentUsersPostId) return;

    this.showLikedUsersModal = false;

    this.authService.getCommentedUsers(this.commentUsersPostId, this.commentUsersPage, 5).subscribe({
      next: (data) => {
        this.currentComments = data.content;
        this.commentUsersTotalPages = data.totalPages;
        this.showCommentsModal = true;
      }
    });
  }

  prevCommentsPage() {
    if (this.commentUsersPage > 0) {
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

  removeComment(id:any){
    //  this.authService.removeComment(id).subscribe({
    //   next: (data) => {
    //     this.showCommentsModal = false;
    //     this.ngOnInit();
    //   },
    //   error: (err) => {
    //     console.log(err);
    //   },
    // });
  }

  closeLikedUsersModal() {
    this.showLikedUsersModal = false;
  }

  closeCommentsModal() {
    this.showCommentsModal = false;
  }
}