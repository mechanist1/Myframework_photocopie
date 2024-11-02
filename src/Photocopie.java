import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Photocopie {

    public void copy(Object from, Object to) {
        Class c = from.getClass();
        while (c != null) {
            for (Field f : c.getDeclaredFields()) {
                copyField(f, from, to);
            }
            c = c.getSuperclass();
        }
    }

    private Accessor resolveAccessor(
            String name, Class<?> declaringClass) {
        Class c = declaringClass;
        Field field = null;
        Method getter = null;
        Method setter = null;
        while (c != null) {
            if (field == null) {
                for (Field f : c.getDeclaredFields()) {
                    if (f.getName().equals(name)) {
                        field = f;
                    }
                }
            }
            String cap = name.substring(0, 1).toUpperCase()
                    + name.substring(1);
            for (Method m : c.getDeclaredMethods()) {
                if (getter == null) {
                    if (m.getName().equals("get" + cap)
                            && m.getParameterCount() == 0) {
                        getter = m;
                    } else if (m.getName().equals("is" + cap)
                            && m.getParameterCount() == 0) {
                        getter = m;
                    }
                }
                if (setter == null) {
                    if (m.getName().equals("set" + cap)
                            && m.getParameterCount() == 0) {
                        setter = m;
                    }
                }
            }
            c = c.getSuperclass();
        }
        if(field!=null || setter!=null || getter!=null){
            return new Accessor(field, getter, setter);
        }
        return null;
    }

    class Accessor {

        Field field;
        Method getter;
        Method setter;

        public Accessor(Field field, Method getter, Method setter) {
            this.field = field;
            this.getter = getter;
            this.setter = setter;
        }

        boolean hasConvert() {
            if (field != null && field.getAnnotation(Convert.class) != null) {
                return true;
            }
            if (getter != null && getter.getAnnotation(Convert.class) != null) {
                return true;
            }
            if (setter != null && setter.getAnnotation(Convert.class) != null) {
                return true;
            }
            return false;
        }
    }

    private void copyField(Field f, Object from, Object to) {
        try {
            Accessor a = resolveAccessor(f.getName(), f.getDeclaringClass());
            Accessor b = resolveAccessor(f.getName(), to.getClass());
            if (a == null || b == null) {
                return;
            }
            boolean convert = a.hasConvert() || b.hasConvert();
            Object value = a.getter.invoke(from);
            if (convert) {
                String t1 = a.field.getType().getName();
                String t2 = b.field.getType().getName();
                if (t1.equals("java.lang.String")
                        && (t2.equals("int"))) {
                    value = value == null ? 0
                            : Integer.parseInt((String) value);
                }
                if (t1.equals("java.lang.String")
                        && t2.equals("java.lang.Integer")) {
                    value = value == null ? null
                            : Integer.parseInt((String) value);
                }
                if (t2.equals("java.lang.String")
                        && (t1.equals("int"))) {
                    value = String.valueOf(value);
                }
                if (t2.equals("java.lang.String")
                        && t1.equals("java.lang.Integer")) {
                    value = String.valueOf(value);
                }
            }
            b.setter.invoke(to, value);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Photocopie.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Photocopie.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Photocopie.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
