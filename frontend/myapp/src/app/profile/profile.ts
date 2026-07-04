import { Component, OnInit, HostListener  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute ,Router } from '@angular/router'; 
import { Authservice } from '../service/authservice';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  providers: [HttpClientModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss'],
})
export class Profile implements OnInit {
  activeTab: 'posts' | 'followers' | 'following' = 'posts';

  user: any = null;
  profileData: any = null;
  editOption: Boolean = false;
  followStatus: Boolean = false;
  isAdmin: boolean = false;
  check: String = "NOT_NULL";

  showEditModal = false;
  editProfile: any = {
    username: '',
    fullname: '',
    bio: '',
    dob: '',
    email: '',
    phone: '',
  };

  // Notification variables
  notificationMessage = '';
  notificationType: 'success' | 'error' | '' = '';
  notificationTimeoutId: any;
  userId: string | null = null;

  selectedImageFile: File | null = null;


  constructor(
    private authService: Authservice,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.followStatus = false;
    const tokenPayload = this.authService.getDataFromToken(localStorage.getItem('token'));
    this.isAdmin = tokenPayload?.role === 'Admin' || localStorage.getItem('role') === 'Admin';

    this.route.queryParamMap.subscribe((params) => {
      this.userId = params.get('id') || tokenPayload?.userId;
      if (this.userId) {
        this.authService.getUserById(this.userId).subscribe({   
          next: (data) => {
            this.user = data;
            this.editOption = data.self
            this.followStatus = data.followStatus;
            this.setProfileData(data);
            this.clearNotification();
          },
          error: (err) => {
            console.log(err)
            this.setNotification(err.error?.message, 'error');
          },
        });
      } else {
        console.log("NO")
        this.setNotification('User ID not found', 'error');
      }
    });
  }

  setProfileData(data: any) {
    this.profileData = {
      username: data.username || 'Not provided',
      fullname: data.fullname || 'Not provided',
      bio: data.bio || 'Not provided',
      dob: data.dob || 'Not provided',
      image: data.image,
      email: data.email || 'Not provided',
      phone: data.phone || 'Not provided',
      postsCount: data.posts || 0,
      followersCount: data.followers || 0,
      followingCount: data.followings || 0,
      posts: data.posts || [],
      followers: [],
      followings: [],
    };
  }

  loadPostData() {
    this.authService.getUserPosts(this.userId).subscribe({
      next: (data) => {
       this.profileData.posts = [...data];
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  loadFollowerData() {
    this.authService.followers(this.userId).subscribe({
      next: (userIds) => {
        this.authService.getUsersByIds(userIds).subscribe({
          next: (users) => {
            this.profileData.followers = [...users];
          },
          error: (e) => {
            this.setNotification(e.error.message, 'error');
          },
        });
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  loadFollowingData() {
    this.authService.followings(this.userId).subscribe({
      next: (userIds) => {
        this.authService.getUsersByIds(userIds).subscribe({
          next: (users) => {
            this.profileData.followings = [...users];
          },
          error: (e) => {
            this.setNotification(e.error.message, 'error');
          },
        });
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  setActiveTab(tab: 'posts' | 'followers' | 'following') {
    this.activeTab = tab;
    if (tab === 'posts' && this.profileData.postsCount > 0) {
      this.loadPostData();
    }
    if (tab === 'followers' && this.profileData.followersCount > 0) {
      this.loadFollowerData();
    }
    if (tab === 'following' && this.profileData.followingCount > 0) {
      this.loadFollowingData();
    }
  }

  openEditProfile() {
    this.editProfile = {
      image: this.profileData.image,
      username: this.profileData.username,
      fullname: this.profileData.fullname,
      bio: this.profileData.bio,
       dob: Array.isArray(this.profileData.dob)
          ? `${this.profileData.dob[0]}-${String(this.profileData.dob[1]).padStart(2,'0')}-${String(this.profileData.dob[2]).padStart(2,'0')}`
          : '',
      email: this.profileData.email,
      phone: this.profileData.phone,
    };
    this.showEditModal = true;
    this.clearNotification();
  }

  closeEditProfile() {
    this.showEditModal = false;
    this.clearNotification();
    this.ngOnInit()
  }

  saveProfile() {
    if (!this.user || !this.user.id) {
      this.setNotification('User ID is missing.', 'error');
      return;
    }

    this.authService.updateUser(this.user.id, this.editProfile, this.selectedImageFile).subscribe({
      next: (updatedUser) => {
        this.setProfileData(updatedUser);
        this.showEditModal = false;
        this.setNotification(updatedUser.message, 'success');
        setTimeout(() => {
          this.ngOnInit();
        }, 1000);
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  // Notification methods
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

  viewAccount(id: any) {
    if (id == localStorage.getItem('id')) {
      this.router.navigate(['/profile']);
    } else {
      this.router.navigate(['/profile'], { queryParams: { id: id } });
    }
  }

  follow(id: any){
      this.authService.follow(id).subscribe({
      next: (res) => {
        this.setNotification(res.message, 'success');
        setTimeout(() => {
          this.ngOnInit();
        }, 1000);
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
    }
    

  unfollow(id: any){
    this.authService.unfollow(id).subscribe({
      next: (res) => {
        this.setNotification(res.message, 'success');
        setTimeout(() => {
          this.ngOnInit();
        }, 1000);
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  delete(id:any){
    this.authService.delete(id).subscribe({
      next: (res) => {
        this.setNotification(res.message, 'success');
        setTimeout(() => {
          this.loadPostData();
          this.ngOnInit();
        }, 1000);
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  onImageSelected(event: Event): void {
  const file = (event.target as HTMLInputElement).files?.[0];
  if (file) {
    this. selectedImageFile = file;
    const reader = new FileReader();
    reader.onload = () => {
      const base64 = (reader.result as string).split(',')[1]; 
      this.editProfile.image = base64;
    };
    reader.readAsDataURL(file);
  }
}

showDownloadOptions = false;
selectedFormat: '' | 'excel' | 'json' | 'zip' = '';

get selectedFormatLabel() {
  switch (this.selectedFormat) {
    case 'excel':
      return 'Excel';
    case 'json':
      return 'JSON';
    case 'zip':
      return 'ZIP';
    default:
      return '';
  }
}

toggleDownloadDropdown(event: Event) {
  event.stopPropagation();
  this.showDownloadOptions = !this.showDownloadOptions;
}

selectDownloadFormat(format: 'excel' | 'json' | 'zip', event: Event) {
  event.stopPropagation();
  this.selectedFormat = format;
  this.showDownloadOptions = false;
  this.downloadProfile(format);
}

@HostListener('document:keydown.escape', ['$event'])
onEscape(event: any) {
  if (this.showEditModal) {
    this.closeEditProfile();
  }
}

// Optional: close dropdown when clicking outside
@HostListener('document:click', ['$event'])
onDocumentClick(event: Event) {
  const target = event.target as HTMLElement;
  if (!target.closest('.profile-download-section')) {
    this.showDownloadOptions = false;
  }
}


downloadProfile(format: 'excel' | 'json' | 'zip') {
  if (!this.userId) {
    this.setNotification('Unable to download: user ID is missing.', 'error');
    return;
  }

  this.authService.download(this.userId, format).subscribe({
    next: (res: Blob) => {
      // Create a blob URL
      const blob = new Blob([res], { type: this.getMimeType(format) });
      const url = window.URL.createObjectURL(blob);

      // Create a temporary link and click it
      const a = document.createElement('a');
      a.href = url;
      a.download = `${this.profileData.username || "user"}'s_profile.${this.getFileExtension(format)}`;
      a.click();

      // Release the object URL
      window.URL.revokeObjectURL(url);
    },
    error: (err) => {
      this.setNotification(err.error?.message || 'Download failed', 'error');
    },
  });
}

// Helper functions
private getMimeType(format: string) {
  switch (format) {
    case 'excel': return 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
    case 'json': return 'application/json';
    case 'zip': return 'application/zip';
    default: return 'application/octet-stream';
  }
}

private getFileExtension(format: string) {
  switch (format) {
    case 'excel': return 'xlsx';
    case 'json': return 'json';
    case 'zip': return 'zip';
    default: return 'bin';
  }
}

}
