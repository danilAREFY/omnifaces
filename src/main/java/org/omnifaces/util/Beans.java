/*
 * Copyright 2013 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.util;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.enterprise.context.NormalScope;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 * <p>
 * Collection of utility methods for the CDI API that are mainly shortcuts for obtaining stuff from the
 * {@link BeanManager}.
 *
 * <h3>Usage</h3>
 * <pre>
 * // Get the CDI managed bean reference of the given bean class.
 * SomeBean someBean = Beans.getReference(SomeBean.class);
 * </pre>
 * <p>
 * The "native" CDI way would otherwise look like this, provided that you can't use {@link Inject} for some
 * technical reason.
 * <pre>
 * // Get the CDI managed bean instance of the given bean class.
 * Set&lt;Bean&lt;?&gt;&gt; beans = beanManager.getBeans(SomeBean.class);
 * Bean&lt;SomeBean&gt; bean = (Bean&lt;SomeBean&gt;) beanManager.resolve(beans);
 * CreationalContext&lt;SomeBean&gt; context = beanManager.createCreationalContext(bean);
 * SomeBean someBean = (SomeBean) beanManager.getReference(bean, SomeBean.class, context);
 * </pre>
 * <p>
 * If you need a dependency-free way of obtaining the CDI managed bean instance (e.g. when you want to write code which
 * should also run on Tomcat), use {@link org.omnifaces.config.BeanManager} enum instead.
 *
 * @author Bauke Scholtz
 * @since 1.6.1
 */
@Typed
public final class Beans {

	/**
	 * Returns the CDI bean manager.
	 * @return The CDI bean manager.
	 * @since 2.0
	 */
	public static BeanManager getManager() {
		return org.omnifaces.config.BeanManager.INSTANCE.get();
	}

	/**
	 * Returns the CDI managed bean representation of the given bean class.
	 * @param <T> The generic CDI managed bean type.
	 * @param beanClass The CDI managed bean class.
	 * @return The CDI managed bean representation of the given bean class, or <code>null</code> if there is none.
	 */
	public static <T> Bean<T> resolve(Class<T> beanClass) {
		return BeansLocal.resolve(getManager(), beanClass);
	}

	/**
	 * Returns the CDI managed bean reference (proxy) of the given class.
	 * Note that this actually returns a client proxy and the underlying actual instance is thus always auto-created.
	 * @param <T> The expected return type.
	 * @param beanClass The CDI managed bean class.
	 * @return The CDI managed bean reference (proxy) of the given class, or <code>null</code> if there is none.
	 */
	public static <T> T getReference(Class<T> beanClass) {
		return BeansLocal.getReference(getManager(), beanClass);
	}

	/**
	 * Returns the CDI managed bean reference (proxy) of the given bean.
	 * Note that this actually returns a client proxy and the underlying actual instance is thus always auto-created.
	 * @param <T> The expected return type.
	 * @param bean The CDI managed bean representation.
	 * @return The CDI managed bean reference (proxy) of the given bean, or <code>null</code> if there is none.
	 */
	public static <T> T getReference(Bean<T> bean) {
		return BeansLocal.getReference(getManager(), bean);
	}

	/**
	 * Returns the CDI managed bean instance (actual) of the given class and creates one if one doesn't exist.
	 * @param <T> The expected return type.
	 * @param beanClass The CDI managed bean class.
	 * @return The CDI managed bean instance (actual) of the given class, or <code>null</code> if there is none.
	 * @since 1.8
	 */
	public static <T> T getInstance(Class<T> beanClass) {
		return BeansLocal.getInstance(getManager(), beanClass);
	}

	/**
	 * Returns the CDI managed bean instance (actual) of the given class and creates one if one doesn't exist and
	 * <code>create</code> argument is <code>true</code>, otherwise don't create one and return <code>null</code> if
	 * there's no current instance.
	 * @param <T> The expected return type.
	 * @param beanClass The CDI managed bean class.
	 * @param create Whether to create create CDI managed bean instance if one doesn't exist.
	 * @return The CDI managed bean instance (actual) of the given class, or <code>null</code> if there is none and/or
	 * the <code>create</code> argument is <code>false</code>.
	 * @since 1.7
	 */
	public static <T> T getInstance(Class<T> beanClass, boolean create) {
		return BeansLocal.getInstance(getManager(), beanClass, create);
	}

	/**
	 * Returns the CDI managed bean instance of the given resolved bean from the given bean manager and creates one if
	 * one doesn't exist and <code>create</code> argument is <code>true</code>, otherwise don't create one and return
	 * <code>null</code> if there's no current instance.
	 * @param <T> The expected return type.
	 * @param bean The CDI managed bean representation.
	 * @param create Whether to create create CDI managed bean instance if one doesn't exist.
	 * @return The CDI managed bean instance (actual) of the given bean, or <code>null</code> if there is none and/or
	 * the <code>create</code> argument is <code>false</code>.
	 * @since 1.7
	 */
	public static <T> T getInstance(Bean<T> bean, boolean create) {
		return BeansLocal.getInstance(getManager(), bean, create);
	}

	/**
	 * Returns all active CDI managed bean instances in the given CDI managed bean scope. The map key represents
	 * the active CDI managed bean instance and the map value represents the CDI managed bean name, if any.
	 * @param <S> The generic CDI managed bean scope type.
	 * @param scope The CDI managed bean scope, e.g. <code>RequestScoped.class</code>.
	 * @return All active CDI managed bean instances in the given CDI managed bean scope.
	 * @since 1.7
	 */
	public static <S extends NormalScope> Map<Object, String> getActiveInstances(Class<S> scope) {
		return BeansLocal.getActiveInstances(getManager(), scope);
	}

	/**
	 * Destroy the currently active instance of the given CDI managed bean class.
	 * @param beanClass The CDI managed bean class.
	 * @since 2.0
	 */
	public static <T> void destroy(Class<T> beanClass) {
		BeansLocal.destroy(getManager(), beanClass);
	}

	/**
	 * Destroy the currently active instance of the given CDI managed bean representation.
	 * @param bean The CDI managed bean representation.
	 * @since 2.0
	 */
	public static <T> void destroy(Bean<T> bean) {
		BeansLocal.destroy(getManager(), bean);
	}

	/**
	 * Get program element annotation of a certain annotation type. The difference with
	 * {@link Annotated#getAnnotation(Class)} is that this method will recursively search inside all {@link Stereotype}
	 * annotations.
	 * @param <A> The generic annotation type.
	 * @param annotated A Java program element that can be annotated.
	 * @param annotationType The class of the annotation type.
	 * @return The program element annotation of the given annotation type if it could be found, otherwise
	 * <code>null</code>.
	 * @since 1.8
	 */
	public static <A extends Annotation> A getAnnotation(Annotated annotated, Class<A> annotationType) {
		return BeansLocal.getAnnotation(getManager(), annotated, annotationType);
	}

}