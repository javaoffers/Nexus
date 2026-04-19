import { tokenAPI } from '@/service/api';

export async function addToken(tokenDesc) {
  return tokenAPI.addToken(tokenDesc);
}

export async function deleteToken(tokenId) {
  return tokenAPI.deleteTokenById(tokenId);
}

export async function updateToken(params) {
  return tokenAPI.updateTokenById(params);
}

export async function queryTokenPage(params) {
  return tokenAPI.tokenPage(params);
}
