package com.grafit.projectGrafit.models;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.*;
import jakarta.persistence.Column;

/**
 * Entidad que representa un usuario en el sistema.
 * Esta clase almacena toda la información personal y biométrica de los usuarios,
 * incluyendo sus datos de acceso, medidas corporales y características anatómicas.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(?: [A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)?$", 
            message = "El nombre debe empezar con mayúscula y el resto en minúsculas. Puede tener un segundo nombre con el mismo formato")
    private String name;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 50, message = "El primer apellido no puede tener más de 50 caracteres")
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(?:[- ][A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)?$", 
            message = "El primer apellido debe empezar con mayúscula y el resto en minúsculas. Puede ser compuesto con guion o espacio")
    private String surname1;

    @Size(max = 50, message = "El segundo apellido no puede tener más de 50 caracteres")
    @Pattern(
        regexp = "^$|^[A-ZÁÉÍÓÚÑ][a-záéíóúñA-ZÁÉÍÓÚÑ\\- ]*$",
        message = "El segundo apellido debe empezar con mayúscula y el resto en minúsculas. Puede ser compuesto con guion o espacio"
    )
    private String surname2;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    @Pattern(regexp = "[a-zA-Z0-9_\\.@]+", message = "El nombre de usuario solo puede contener letras, números, guiones bajos, puntos y @")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede tener más de 100 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @Pattern(regexp = "[0-9]{9}", message = "El teléfono debe tener exactamente 9 dígitos")
    private String phone;

    @NotNull(message = "El género es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10)
    private Role role = Role.USER;

    @DecimalMin(value = "1.0", message = "La altura debe ser al menos 1 metro")
    private Double heightMeters;  // Ya está correcto como Double

    @Min(value = 20, message = "El peso debe ser al menos 20 kg")
    @Max(value = 300, message = "El peso no puede ser mayor a 300 kg")
    private Double weightKg;  // Cambiar de float a Double para consistencia

    @Transient
    private float imc;

    private LocalDate dateOfRegister;

    // Características anatómicas
    @Min(value = 0, message = "La longitud de brazos no puede ser negativa")
    private Float armLength;

    @Min(value = 0, message = "La longitud del torso no puede ser negativa")
    private Float torsoLength;

    @Min(value = 0, message = "La longitud del fémur no puede ser negativa")
    private Float femurLength;

    @Min(value = 0, message = "La longitud de la tibia no puede ser negativa")
    private Float tibiaLength;

    /**
     * Enumeración que representa los posibles géneros de un usuario.
     */
    public enum Gender {
        MASCULINO,
        FEMENINO,
        OTRO
    }

    /**
     * Enumeración que representa las unidades de medida para la altura.
     */
    public enum HeightUnit {
        METERS, FEET
    }

    /**
     * Enumeración que representa las unidades de medida para el peso.
     */
    public enum WeightUnit {
        KG, LBS
    }

    /**
     * Enumeración que representa los roles de usuario en el sistema.
     */
    public enum Role {
        USER,
        ADMIN
    }

    /**
     * Constructor por defecto que inicializa un nuevo usuario con rol USER.
     */
    public User() {
        this.role = Role.USER;
    }

    /**
     * Constructor para crear un nuevo usuario con todos sus datos básicos.
     *
     * @param name Nombre del usuario
     * @param surname1 Primer apellido
     * @param surname2 Segundo apellido
     * @param username Nombre de usuario para el sistema
     * @param email Correo electrónico
     * @param password Contraseña
     * @param phone Número de teléfono
     * @param gender Género del usuario
     * @param role Rol en el sistema
     * @param height Altura
     * @param heightUnit Unidad de medida de la altura
     * @param weight Peso
     * @param weightUnit Unidad de medida del peso
     */
    public User(
            String name,
            String surname1,
            String surname2,
            String username,
            String email,
            String password,
            String phone,
            Gender gender,
            Role role,
            Double height,
            HeightUnit heightUnit,
            Double weight,
            WeightUnit weightUnit) {
        this.name = name;
        this.surname1 = surname1;
        this.surname2 = surname2;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.role = role != null ? role : Role.USER;
        setHeight(height, heightUnit);
        setWeight(weight, weightUnit);
        this.dateOfRegister = LocalDate.now();
    }

    /**
     * Constructor protegido para crear un usuario con ID específico.
     * Este constructor es principalmente para uso interno, pruebas y mapeos.
     *
     * @param idUser ID del usuario
     * @param name Nombre del usuario
     * @param surname1 Primer apellido
     * @param surname2 Segundo apellido
     * @param username Nombre de usuario
     * @param email Correo electrónico
     * @param password Contraseña
     * @param phone Número de teléfono
     * @param gender Género del usuario
     * @param role Rol en el sistema
     * @param height Altura
     * @param heightUnit Unidad de medida de la altura
     * @param weight Peso
     * @param weightUnit Unidad de medida del peso
     * @param dateOfRegister Fecha de registro
     */
    protected User(
            Long idUser,
            String name,
            String surname1,
            String surname2,
            String username,
            String email,
            String password,
            String phone,
            Gender gender,
            Role role,
            Double height,
            HeightUnit heightUnit,
            Double weight,
            WeightUnit weightUnit,
            LocalDate dateOfRegister) {
        this(name, surname1, surname2, username, email, password, phone, gender, role, height, heightUnit, weight,
                weightUnit);
        this.id = idUser;
        this.dateOfRegister = dateOfRegister;
    }

    /**
     * Método ejecutado antes de persistir el usuario.
     * Establece la fecha de registro si no está definida.
     */
    @PrePersist
    protected void onCreate() {
        if (dateOfRegister == null) {
            dateOfRegister = LocalDate.now();
        }
    }

    /**
     * Obtiene el ID del usuario.
     * @return El ID único del usuario
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID del usuario.
     * @param id El ID a establecer
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del usuario.
     * @return El nombre del usuario
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del usuario.
     * @param name El nombre a establecer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene el primer apellido del usuario.
     * @return El primer apellido
     */
    public String getSurname1() {
        return surname1;
    }

    /**
     * Establece el primer apellido del usuario.
     * @param surname1 El primer apellido a establecer
     */
    public void setSurname1(String surname1) {
        this.surname1 = surname1;
    }

    /**
     * Obtiene el segundo apellido del usuario.
     * @return El segundo apellido
     */
    public String getSurname2() {
        return surname2;
    }

    /**
     * Establece el segundo apellido del usuario.
     * @param surname2 El segundo apellido a establecer
     */
    public void setSurname2(String surname2) {
        this.surname2 = surname2;
    }

    /**
     * Obtiene el nombre de usuario del sistema.
     * @return El nombre de usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario del sistema.
     * @param username El nombre de usuario a establecer
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     * @return El correo electrónico
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     * @param email El correo electrónico a establecer
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña del usuario.
     * @return La contraseña
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     * @param password La contraseña a establecer
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene el número de teléfono del usuario.
     * @return El número de teléfono
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Establece el número de teléfono del usuario.
     * @param phone El número de teléfono a establecer
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Obtiene el género del usuario.
     * @return El género del usuario
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Establece el género del usuario.
     * @param gender El género a establecer
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Obtiene el rol del usuario en el sistema.
     * @return El rol del usuario
     */
    public Role getRole() {
        return role;
    }

    /**
     * Establece el rol del usuario en el sistema.
     * @param role El rol a establecer
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Obtiene la altura del usuario en metros.
     * @return La altura en metros
     */
    public double getHeightMeters() {
        return heightMeters;
    }

    /**
     * Obtiene la altura del usuario en pies.
     * @return La altura en pies
     */
    public Double getHeightInFeet() {
        return heightMeters * 3.28084;
    }

    /**
     * Obtiene el peso del usuario en kilogramos.
     * @return El peso en kilogramos
     */
    public double getWeightKg() {
        return weightKg;
    }

    /**
     * Obtiene el peso del usuario en libras.
     * @return El peso en libras
     */
    public Double getWeightInLbs() {
        return weightKg * 2.20462;
    }

    /**
     * Obtiene el Índice de Masa Corporal (IMC) del usuario.
     * @return El IMC calculado
     */
    public float getImc() {
        calcularImc();
        return imc;
    }

    /**
     * Obtiene la fecha de registro del usuario.
     * @return La fecha de registro
     */
    public LocalDate getDateOfRegister() {
        return dateOfRegister;
    }

    /**
     * Establece la fecha de registro del usuario.
     * @param dateOfRegister La fecha de registro a establecer
     */
    public void setDateOfRegister(LocalDate dateOfRegister) {
        this.dateOfRegister = dateOfRegister;
    }

    /**
     * Establece la altura del usuario en metros.
     * @param heightMeters La altura en metros a establecer
     */
    public void setHeightMeters(Double heightMeters) {
        this.heightMeters = heightMeters;
        calcularImc();
    }

    /**
     * Establece el peso del usuario en kilogramos.
     * @param weightKg El peso en kilogramos a establecer
     */
    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
        calcularImc();
    }

    /**
     * Establece la altura del usuario en la unidad especificada.
     * @param height La altura a establecer
     * @param unit La unidad de medida (METERS o FEET)
     */
    public void setHeight(Double height, HeightUnit unit) {
        this.heightMeters = (unit == HeightUnit.FEET) ? height / 3.28084 : height;
        calcularImc();
    }

    /**
     * Establece el peso del usuario en la unidad especificada.
     * @param weight El peso a establecer
     * @param unit La unidad de medida (KG o LBS)
     */
    public void setWeight(Double weight, WeightUnit unit) {
        this.weightKg = (unit == WeightUnit.LBS) ? weight / 2.20462 : weight;
        calcularImc();
    }

    /**
     * Calcula el IMC del usuario basado en su peso y altura actuales.
     */
    private void calcularImc() {
        if (heightMeters != null && heightMeters > 0 && weightKg != null) {
            this.imc = (float) (weightKg / (heightMeters * heightMeters));
        } else {
            this.imc = 0;
        }
    }

    /**
     * Obtiene la longitud de los brazos del usuario.
     * @return La longitud de los brazos en la unidad establecida
     */
    public Float getArmLength() {
        return armLength;
    }

    /**
     * Establece la longitud de los brazos del usuario.
     * @param armLength La longitud de los brazos a establecer
     */
    public void setArmLength(Float armLength) {
        this.armLength = armLength;
    }

    /**
     * Obtiene la longitud del torso del usuario.
     * @return La longitud del torso en la unidad establecida
     */
    public Float getTorsoLength() {
        return torsoLength;
    }

    /**
     * Establece la longitud del torso del usuario.
     * @param torsoLength La longitud del torso a establecer
     */
    public void setTorsoLength(Float torsoLength) {
        this.torsoLength = torsoLength;
    }

    /**
     * Obtiene la longitud del fémur del usuario.
     * @return La longitud del fémur en la unidad establecida
     */
    public Float getFemurLength() {
        return femurLength;
    }

    /**
     * Establece la longitud del fémur del usuario.
     * @param femurLength La longitud del fémur a establecer
     */
    public void setFemurLength(Float femurLength) {
        this.femurLength = femurLength;
    }

    /**
     * Obtiene la longitud de la tibia del usuario.
     * @return La longitud de la tibia en la unidad establecida
     */
    public Float getTibiaLength() {
        return tibiaLength;
    }

    /**
     * Establece la longitud de la tibia del usuario.
     * @param tibiaLength La longitud de la tibia a establecer
     */
    public void setTibiaLength(Float tibiaLength) {
        this.tibiaLength = tibiaLength;
    }
}
