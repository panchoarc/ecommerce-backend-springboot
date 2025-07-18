INSERT INTO public.category (is_active, category_id, name, description) VALUES (true, 43, 'Ropa', 'Ropa');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 39, '/auth', 'callback', 'GET', '/auth/callback');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 40, '/v3', 'api-docs/swagger-config', 'GET', '/v3/api-docs/swagger-config');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 41, '/auth', 'provider/{provider}', 'GET', '/auth/provider/{provider}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 42, '/auth', 'register', 'POST', '/auth/register');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 43, '/auth', 'login', 'POST', '/auth/login');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 44, '/swagger-ui.html', '', 'GET', '/swagger-ui.html');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 45, '/v3', 'api-docs', 'GET', '/v3/api-docs');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 27, '/products', 'search', 'POST', '/products/search');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 6, '/category', '{id}', 'GET', '/category/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 7, '/category', 'search', 'POST', '/category/search');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 16, '/products', '{id}', 'GET', '/products/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 31, '/products', '{id}/images/{imgId}', 'DELETE', '/products/{id}/images/{imgId}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 3, '/address', '{id}', 'PUT', '/address/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 9, '/reviews', '', 'POST', '/reviews');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 11, '/address', '{id}', 'DELETE', '/address/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 14, '/address', 'search', 'GET', '/address/search');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 15, '/orders', '{id}', 'GET', '/orders/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 19, '/orders', '', 'POST', '/orders');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 21, '/address', '{id}', 'GET', '/address/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 22, '/address', '', 'POST', '/address');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 25, '/reviews', '{id}', 'DELETE', '/reviews/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 29, '/reviews', '{id}', 'GET', '/reviews/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 30, '/orders', '', 'GET', '/orders');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 2, '/v3', 'api-docs.yaml', 'GET', '/v3/api-docs.yaml');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 4, '/category', '', 'POST', '/category');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 5, '/products', '{id}', 'DELETE', '/products/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 10, '/endpoints', '{id}', 'PUT', '/endpoints/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 12, '/products', '', 'POST', '/products');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 13, '/category', '{id}', 'PUT', '/category/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 17, '/products', '{id}/images', 'POST', '/products/{id}/images');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 18, '/roles', '{id}/endpoints', 'POST', '/roles/{id}/endpoints');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 20, '/category', '{id}', 'DELETE', '/category/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 23, '/products', '{id}', 'PUT', '/products/{id}');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 24, '/roles', 'sync', 'POST', '/roles/sync');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 26, '/endpoints', 'sync', 'POST', '/endpoints/sync');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 28, '/backup', 'create', 'POST', '/backup/create');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 8, '/products', '{id}/images', 'GET', '/products/{id}/images');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 33, '/auth', 'logout', 'POST', '/auth/logout');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 35, '/users', 'me', 'GET', '/users/me');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 38, '/auth', 'refresh-token', 'GET', '/auth/refresh-token');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 47, '/category', '{id}/attributes', 'POST', '/category/{id}/attributes');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 49, '/category', '{id}/attributes', 'PUT', '/category/{id}/attributes');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 50, '/orders', '{id}/vouchers', 'GET', '/orders/{id}/vouchers');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 51, '/payments', 'create', 'POST', '/payments/create');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 52, '/category', '', 'GET', '/category');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 48, '/category', '{id}/attributes', 'GET', '/category/{id}/attributes');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 53, '/products', '{id}/attributes', 'POST', '/products/{id}/attributes');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, false, 54, '/products', '{id}/attributes', 'GET', '/products/{id}/attributes');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 36, '/recommendations', '{productId}/similar', 'GET', '/recommendations/{productId}/similar');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 37, '/recommendations', '', 'GET', '/recommendations');
INSERT INTO public.endpoint (is_active, is_public, id, base_path, dynamic_path, http_method, url) VALUES (true, true, 1, '/products', '{id}/reviews', 'GET', '/products/{id}/reviews');
INSERT INTO public.role (is_active, id, external_id, name) VALUES (true, 1, '843d9d89-e362-42bb-8cf5-889c47b0c70e', 'user');
INSERT INTO public.role (is_active, id, external_id, name) VALUES (true, 2, 'dad3ae27-d39e-4ed4-be7b-3e57edb1e32e', 'admin');
INSERT INTO public.role (is_active, id, external_id, name) VALUES (true, 3, 'dca9785b-6205-493a-885b-5fa36d772efa', 'uma_protection');
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 1, 1, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 2, 2, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 3, 3, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 4, 4, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 5, 5, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 6, 6, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 7, 7, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 8, 8, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 9, 9, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 10, 10, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 11, 11, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 12, 12, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 13, 13, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 14, 14, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 15, 15, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 16, 16, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 17, 17, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 18, 18, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 19, 19, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 20, 20, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 21, 21, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 22, 22, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 23, 23, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 24, 24, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 25, 25, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 26, 26, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 27, 27, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 28, 28, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 29, 29, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 30, 30, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 1, 31, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 3, 32, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 6, 33, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 7, 34, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 8, 35, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 9, 36, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 11, 37, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 14, 38, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 15, 39, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 16, 40, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 19, 41, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 21, 42, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 22, 43, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 25, 44, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 27, 45, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 29, 46, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 30, 47, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 35, 48, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 35, 49, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 47, 51, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 49, 53, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 50, 54, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 50, 55, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 51, 56, 1);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 51, 57, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 53, 58, 2);
INSERT INTO public.role_endpoint (is_active, endpoint_id, id, rol_id) VALUES (true, 54, 59, 2);
