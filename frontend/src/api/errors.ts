import { ApiError } from "./client";

export function errorMessage(e: unknown): string {
  if (e instanceof ApiError) {
    return e.details.length > 0 ? `${e.message}: ${e.details.join(", ")}` : e.message;
  }
  return "Ocurrio un error inesperado";
}