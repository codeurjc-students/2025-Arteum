/**
 * OpenAPI definition
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { ArtworkRequestModel } from './artworkRequest';


export interface UserProfileResponseModel {
    username: string;
    reviews: never[];
    reviews_avg: number;
    followers: number;
    follows: number;
    artworks: never[];
    admin: any;
    logged: any;
    isNotMyProfile: any;
    isFollowing: any; 
    id?: number;
    name?: string;
    email?: string;
    biography?: string;
    location?: string;
    createdAt?: string;
    followersCount?: number;
    followingCount?: number;
    reviewsCount?: number;
    reviewsAverage?: number;
    favoriteArtworks?: Array<ArtworkRequestModel>;
    notMyProfile?: boolean;
}

