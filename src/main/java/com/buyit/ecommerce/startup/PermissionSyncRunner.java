package com.buyit.ecommerce.startup;

import com.buyit.ecommerce.anotations.RequirePermission;
import com.buyit.ecommerce.entity.Permission;
import com.buyit.ecommerce.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionSyncRunner{

    private final ApplicationContext applicationContext;
    private final PermissionRepository permissionRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        syncPermissions();
    }

    private void syncPermissions() {

        applicationContext.getBeansWithAnnotation(RestController.class)
                .forEach((beanName, bean) -> {

                    Class<?> clazz = AopUtils.getTargetClass(bean);

                    // Clase
                    RequirePermission rpClass =
                            AnnotatedElementUtils.findMergedAnnotation(clazz, RequirePermission.class);

                    if (rpClass != null) {
                        savePermission(rpClass.value());
                    }

                    // Métodos
                    for (Method method : clazz.getMethods()) {

                        RequirePermission rp =
                                AnnotatedElementUtils.findMergedAnnotation(method, RequirePermission.class);

                        if (rp != null) {
                            savePermission(rp.value());
                        }
                    }
                });
    }

    private void savePermission(String permissionName) {
        if (!permissionRepository.existsByCode(permissionName)) {

            Permission permission = new Permission();
            permission.setCode(permissionName);
            permissionRepository.save(permission);
        }
    }
}
