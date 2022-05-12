package server.collections;

import server.annotations.Larger;
import server.annotations.Less;
import server.annotations.NotNull;
import server.annotations.Nullable;
import server.exceptions.AnnotationsException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public abstract class Entity {
    public abstract String getInfo();

    public Field[] getAllFields() {
        return this.getClass().getDeclaredFields();
    }

    public Object getValueField(Field field) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(this);
    }

    public void setValueField(Field field, String value)
            throws IllegalAccessException,
            AnnotationsException {
        field.setAccessible(true);
        Class<?> type = field.getType();

        if (field.getAnnotation(NotNull.class) != null) {
            if (value.equals(""))
                throw new AnnotationsException("это значение обязательное");
        } else if (field.getAnnotation(Nullable.class) != null) {
            if (value.equals(""))
                return;
        }

        Object parseData = this.parseValueByString(value, type);

        if ((field.getAnnotation(Less.class) != null || field.getAnnotation(Larger.class) != null) &&
                !(parseData instanceof Number))
            throw new AnnotationsException("значение должно быть числом");
        if (field.getAnnotation(Less.class) != null) {
            int valueAnnotation = field.getAnnotation(Less.class).value();
            boolean isExcept = false;
            if (parseData instanceof Float || parseData instanceof Double) {
                if ((double) parseData >= (double) valueAnnotation)
                    isExcept = true;
            } else if ((long) parseData >= valueAnnotation) {
                isExcept = true;
            }
            if (isExcept)
                throw new AnnotationsException(
                        String.format("значение не может быть больше %s", field.getAnnotation(Less.class).value()));
        } else if (field.getAnnotation(Larger.class) != null) {
            int valueAnnotation = field.getAnnotation(Larger.class).value();
            boolean isExcept = false;
            if (parseData instanceof Float || parseData instanceof Double) {
                if ((double) parseData <= (double) valueAnnotation)
                    isExcept = true;
            } else if ((long) parseData <= valueAnnotation) {
                isExcept = true;
            }
            if (isExcept)
                throw new AnnotationsException(
                        String.format("значение не может быть меньше %s", field.getAnnotation(Larger.class).value()));
        }

        field.set(this, parseData);
    }

    public void setValueField(Field field, Entity value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(this, value);
    }

    public boolean isSubEntity(Field field)
            throws IllegalAccessException,
            NoSuchFieldException {
        field.setAccessible(true);
        if (field.getType().isPrimitive())
            return false;
        return field.getType().getSuperclass().equals(SubEntity.class);
    }

    public boolean isNullField(Field field)
            throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(this) == null ||
                field.get(this).equals(0.0) ||
                field.get(this).equals(0.0F) ||
                field.get(this).equals(0) ||
                field.get(this).equals(0L) ||
                field.get(this).equals("");
    }

    public ArrayList<String> getListStringFields()
            throws IllegalAccessException,
            NoSuchFieldException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException {
        ArrayList<String> allValues = new ArrayList<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (this.isSubEntity(field)) {
                SubEntity value = (SubEntity) field.get(this);
                if (this.isNullField(field)) {
                    value = (SubEntity) field.getType().getConstructor().newInstance();
                }
                allValues.addAll(value.getListStringFields());
            } else {
                allValues.add((!this.isNullField(field)) ? String.valueOf(field.get(this)) : "");
            }
        }
        return allValues;
    }

    public void setFieldsByListString(ArrayList<String> allValues)
            throws IllegalAccessException,
            NoSuchFieldException,
            AnnotationsException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException {
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (this.isSubEntity(field)) {
                Entity subEntity = (SubEntity) field.getType().getConstructor().newInstance();
                subEntity.setFieldsByListString(allValues);
                this.setValueField(field, subEntity);
            } else {
                String value = allValues.get(0);
                allValues.remove(0);
                if (value.equals(""))
                    continue;
                this.setValueField(field, value);
            }
        }
    }

    protected Object parseValueByString(String data, Class<?> type) {
        Object parseData = data;
        if (type.isEnum()) {
            for (Object enumElement : type.getEnumConstants()) {
                if (enumElement.toString().equals(data.toUpperCase())) {
                    parseData = enumElement;
                    break;
                }
            }
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            parseData = Integer.parseInt(data);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            parseData = Long.parseLong(data);
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            parseData = Float.parseFloat(data);
        } else if ((type.equals(Double.class) || type.equals(double.class))) {
            parseData = Double.parseDouble(data);
        } else if ((type.equals(ZonedDateTime.class))) {
            parseData = ZonedDateTime.parse(data);
        }
        return parseData;
    }
}
