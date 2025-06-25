import { Component, OnInit } from '@angular/core';
import { UsersService } from '../api-client/api/users.service';

@Component({
  selector: 'app-user-followers',
  templateUrl: './user-followers.component.html',
  styleUrls: ['./user-followers.component.css']
})
export class UserFollowersComponent implements OnInit {
  userName: string = '';
  range: string = '';
  totalFollowers: number = 0;
  followersEmpty: boolean = true;
  followersPage: any[] = [];
  sortCreatedAt: boolean = true;
  sortNameAsc: boolean = false;
  sortName: boolean = false;
  previousPage: number | null = null;
  sort: string = 'createdAt';
  currentPage: number = 1;
  nextPage: number | null = null;
  pageSize: number = 12;

  constructor(private usersService: UsersService) {}

  ngOnInit(): void {
    this.loadFollowers();
  }

  loadFollowers(): void {
    this.usersService.getUserFollowers(this.currentPage, this.pageSize, this.sort).subscribe({
      next: (followers) => {
        this.followersPage = followers || [];
        this.totalFollowers = this.followersPage.length;
        this.range = this.followersPage.length > 0 ? `1-${this.followersPage.length}` : '0';
        this.followersEmpty = this.followersPage.length === 0;
        // Pagination logic (dummy, adjust as needed)
        this.previousPage = this.currentPage > 1 ? this.currentPage - 1 : null;
        this.nextPage = this.followersPage.length === this.pageSize ? this.currentPage + 1 : null;
      },
      error: () => {
        this.followersPage = [];
        this.followersEmpty = true;
      }
    });
  }

  onSortChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    this.sort = select.value;
    this.sortCreatedAt = this.sort === 'createdAt';
    this.sortNameAsc = this.sort === 'nameAsc';
    this.sortName = this.sort === 'nameDesc';
    this.currentPage = 1;
    this.loadFollowers();
  }
}
