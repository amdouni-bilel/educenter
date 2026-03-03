package com.beedigital.educenter.config;

import com.beedigital.educenter.entity.Role;
import com.beedigital.educenter.entity.User;
import com.beedigital.educenter.enums.RoleEnum;
import com.beedigital.educenter.repositories.RoleRepository;
import com.beedigital.educenter.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Créer les rôles s'ils n'existent pas
        createRolesIfNotExist();

        // Créer le SUPER_ADMIN s'il n'existe pas
        createSuperAdminIfNotExist();
    }

    /**
     * Crée les 4 rôles du système
     */
    private void createRolesIfNotExist() {
        // SUPER_ADMIN
        if (roleRepository.findByCode(RoleEnum.SUPER_ADMIN) == null) {
            Role superAdminRole = Role.builder()
                    .code(RoleEnum.SUPER_ADMIN)
                    .label("Administrateur Système")
                    .description("Accès total au système. Gestion complète des utilisateurs et configuration.")
                    .niveau(1)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            roleRepository.save(superAdminRole);
            System.out.println("✅ Rôle SUPER_ADMIN créé");
        }

        // REGISTRAR
        if (roleRepository.findByCode(RoleEnum.REGISTRAR) == null) {
            Role registrarRole = Role.builder()
                    .code(RoleEnum.REGISTRAR)
                    .label("Agent de Scolarité")
                    .description("Gestion administrative et pédagogique. Validation inscriptions, emploi du temps, notes, absences.")
                    .niveau(2)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            roleRepository.save(registrarRole);
            System.out.println("✅ Rôle REGISTRAR créé");
        }

        // TEACHER
        if (roleRepository.findByCode(RoleEnum.TEACHER) == null) {
            Role teacherRole = Role.builder()
                    .code(RoleEnum.TEACHER)
                    .label("Enseignant")
                    .description("Acteur pédagogique. Saisie notes/absences, consultation emploi du temps.")
                    .niveau(3)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            roleRepository.save(teacherRole);
            System.out.println("✅ Rôle TEACHER créé");
        }

        // STUDENT
        if (roleRepository.findByCode(RoleEnum.STUDENT) == null) {
            Role studentRole = Role.builder()
                    .code(RoleEnum.STUDENT)
                    .label("Étudiant")
                    .description("Bénéficiaire des services. Inscription, consultation notes/absences/emploi du temps.")
                    .niveau(4)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            roleRepository.save(studentRole);
            System.out.println("✅ Rôle STUDENT créé");
        }
    }

    /**
     * Crée le SUPER_ADMIN au premier lancement
     *
     * Email: admin@educenter.tn
     * Password: Admin@123456
     */
    private void createSuperAdminIfNotExist() {
        // Vérifier si le SUPER_ADMIN existe déjà
        if (userRepository.findByUsername("admin") == null) {
            // Récupérer le rôle SUPER_ADMIN
            Role superAdminRole = roleRepository.findByCode(RoleEnum.SUPER_ADMIN);

            if (superAdminRole != null) {
                // Créer l'utilisateur SUPER_ADMIN
                User superAdmin = User.builder()
                        .username("admin")
                        .email("admin@educenter.tn")
                        .password(passwordEncoder.encode("Admin@123456"))  // 🔒 Mot de passe hashé
                        .firstName("Super")
                        .lastName("Administrator")
                        .phone("+216 99 999 999")
                        .address("Université, Tunis")
                        .role(superAdminRole)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .build();

                userRepository.save(superAdmin);

                System.out.println("═══════════════════════════════════════════════════");
                System.out.println("✅ SUPER_ADMIN CRÉÉ AVEC SUCCÈS!");
                System.out.println("═══════════════════════════════════════════════════");
                System.out.println("📧 Email: admin@educenter.tn");
                System.out.println("🔐 Password: Admin@123456");
                System.out.println("🔑 Role: SUPER_ADMIN");
                System.out.println("═══════════════════════════════════════════════════");
            }
        } else {
            System.out.println("ℹ️ SUPER_ADMIN existe déjà");
        }
    }
}
