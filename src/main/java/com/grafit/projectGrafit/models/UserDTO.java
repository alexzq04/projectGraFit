package com.grafit.projectGrafit.models;

import jakarta.validation.constraints.*;
import com.grafit.projectGrafit.models.User.HeightUnit;
import com.grafit.projectGrafit.models.User.WeightUnit;
import com.grafit.projectGrafit.models.User.Gender;

/**
 * Clase de transferencia de datos (DTO) para el manejo de información de usuarios.
 * Esta clase se utiliza principalmente para la creación y actualización de usuarios,
 * incluyendo validaciones específicas como la confirmación de contraseña.
 */
public class UserDTO {

    private Long idUser;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El primer apellido es obligatorio")
    private String surname1;

    private String surname2;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @Email(message = "Debe ser un correo válido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "Confirmar contraseña es obligatorio")
    private String confirmPassword;

    @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
    private String phone;

    @NotNull(message = "El género es obligatorio")
    private Gender gender;

    @NotNull(message = "La altura es obligatoria")
    @DecimalMin(value = "0.1", message = "Altura mínima 0.1")
    private Float height;

    @NotNull(message = "La unidad de altura es obligatoria")
    private HeightUnit heightUnit;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.1", message = "Peso mínimo 0.1")
    private Float weight;

    @NotNull(message = "La unidad de peso es obligatoria")
    private WeightUnit weightUnit;

    /**
     * Verifica si las contraseñas ingresadas coinciden.
     * Este método es utilizado por la validación de Bean Validation.
     * 
     * @return true si las contraseñas coinciden, false en caso contrario
     */
    @AssertTrue(message = "Las contraseñas no coinciden")
    public boolean isPasswordsMatching() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    /**
     * Obtiene el ID del usuario.
     * @return El ID del usuario
     */
    public Long getIdUser() {
        return idUser;
    }

    /**
     * Establece el ID del usuario.
     * @param idUser El ID a establecer
     */
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
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
     * Obtiene el nombre de usuario.
     * @return El nombre de usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario.
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
     * Obtiene la confirmación de contraseña.
     * @return La confirmación de contraseña
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * Establece la confirmación de contraseña.
     * @param confirmPassword La confirmación de contraseña a establecer
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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
     * Obtiene la altura del usuario.
     * @return La altura del usuario
     */
    public Float getHeight() {
        return height;
    }

    /**
     * Establece la altura del usuario.
     * @param height La altura a establecer
     */
    public void setHeight(Float height) {
        this.height = height;
    }

    /**
     * Obtiene la unidad de medida de la altura.
     * @return La unidad de medida de la altura
     */
    public HeightUnit getHeightUnit() {
        return heightUnit;
    }

    /**
     * Establece la unidad de medida de la altura.
     * @param heightUnit La unidad de medida a establecer
     */
    public void setHeightUnit(HeightUnit heightUnit) {
        this.heightUnit = heightUnit;
    }

    /**
     * Obtiene el peso del usuario.
     * @return El peso del usuario
     */
    public Float getWeight() {
        return weight;
    }

    /**
     * Establece el peso del usuario.
     * @param weight El peso a establecer
     */
    public void setWeight(Float weight) {
        this.weight = weight;
    }

    /**
     * Obtiene la unidad de medida del peso.
     * @return La unidad de medida del peso
     */
    public WeightUnit getWeightUnit() {
        return weightUnit;
    }

    /**
     * Establece la unidad de medida del peso.
     * @param weightUnit La unidad de medida a establecer
     */
    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    /**
     * Convierte el DTO a una entidad User.
     * Este método crea una nueva instancia de User con los datos del DTO,
     * estableciendo el rol como null para que sea manejado por la capa de servicio.
     * 
     * @return Una nueva instancia de User con los datos del DTO
     */
    public User toUserEntity() {
        return new User(
                name,
                surname1,
                surname2,
                username,
                email,
                password,
                phone,
                gender,
                null,
                height,
                heightUnit,
                weight,
                weightUnit);
    }
}
