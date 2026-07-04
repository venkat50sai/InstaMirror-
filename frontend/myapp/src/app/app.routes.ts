import { Routes } from '@angular/router';
import { Profile } from './profile/profile';
import { Search } from './search/search';
import { Home } from './home/home';
import { authGuard } from './auth-guard';
import { Admin } from './admin/admin';
import { AdminUsers } from './admin/admin-users/admin-users';
import { AdminPosts } from './admin/admin-posts/admin-posts';
import { AdminHome } from './admin/admin-home/admin-home';
import { AdminLanding } from './admin/admin-landing/admin-landing';
import { Shopping } from './shopping/shopping';
import { Products } from './admin/products/products';

export const routes: Routes = [
  {
    path: 'profile',
    component: Profile,
    canActivate: [authGuard],
  },
  {
    path: 'search',
    component: Search,
    canActivate: [authGuard],
  },
  {
    path: 'home',
    component: Home,
    canActivate: [authGuard],
  },
  {
    path: 'shopping',
    component: Shopping,
    canActivate: [authGuard],
  },
  {
    path: 'admin',
    component: Admin,
    children: [
      { path: '', component: AdminLanding },
      { path: 'home', component: AdminHome },
      { path: 'users', component: AdminUsers },
      { path: 'posts', component: AdminPosts },
      { path: 'products', component: Products },
    ],
  },
  {
    path: '#',
    redirectTo: '/',
    pathMatch: 'full',
  },
];
