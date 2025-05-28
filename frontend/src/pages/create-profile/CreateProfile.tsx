import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ProfileService } from '../../service/ProfileService';
import { useTags } from '../../contexts/TagContext';
import styles from './CreateProfile.module.css';
import { useUser } from '../../contexts/UserContext';

const CreateProfile: React.FC = () => {
    const [formData, setFormData] = useState({
        username: '',
        fullName: '',
        description: '',
        profilePicture: null as File | null,
        tags: [] as string[],
    });
    const [error, setError] = useState<string | null>(null);
    const { tags } = useTags();
    const navigate = useNavigate();
    const {user, setHasProfile} = useUser();


    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0] || null;
        setFormData((prev) => ({
            ...prev,
            profilePicture: file,
        }));
    };

    const handleTagChange = (tagName: string) => {
        setFormData((prev) => {
            const newTags = prev.tags.includes(tagName)
                ? prev.tags.filter((t) => t !== tagName)
                : [...prev.tags, tagName];
            return { ...prev, tags: newTags };
        });
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);

        const data = new FormData();
        data.append('username', user ? user.username : "");
        data.append('fullName', formData.fullName);
        data.append('description', formData.description);
        if (formData.profilePicture) {
            data.append('profilePicture', formData.profilePicture);
        }
        formData.tags.forEach((tag, index) => {
            data.append(`tags[${index}]`, tag);
        });

        try {
            await ProfileService.createProfile(data);
            setHasProfile(true);
            navigate('/');
        } catch (error) {
            setError('Failed to create profile. Please try again.');
        }
    };

    return (
        <div className={styles.createProfileContainer}>
            <div className={styles.formHeader}>
                <h2>Create Profile</h2>
            </div>
            <form className={styles.form} onSubmit={handleSubmit}>
                <div className={styles.formGroup}>
                    <label htmlFor="fullName">Full Name</label>
                    <input
                        type="text"
                        id="fullName"
                        name="fullName"
                        placeholder="Enter full name"
                        className={styles.input}
                        value={formData.fullName}
                        onChange={handleInputChange}
                        required
                    />
                </div>
                <div className={styles.formGroup}>
                    <label htmlFor="description">Description</label>
                    <textarea
                        id="description"
                        name="description"
                        placeholder="Enter description"
                        className={styles.textarea}
                        value={formData.description}
                        onChange={handleInputChange}
                    />
                </div>
                <div className={styles.formGroup}>
                    <label htmlFor="profilePicture">Profile Picture</label>
                    <div className={styles.fileInputWrapper}>
                        <input
                            type="file"
                            id="profilePicture"
                            name="profilePicture"
                            accept="image/*"
                            className={styles.fileInput}
                            onChange={handleFileChange}
                        />
                        <label htmlFor="profilePicture" className={styles.fileInputButton}>
                            Choose File
                        </label>
                    </div>
                </div>
                <div className={styles.formGroup}>
                    <label>Tags</label>
                    <div className={styles.tagList}>
                        {tags.map((tag) => (
                            <label key={tag.id} className={styles.tagLabel}>
                                <input
                                    type="checkbox"
                                    checked={formData.tags.includes(tag.name)}
                                    onChange={() => handleTagChange(tag.name)}
                                />
                                <span className={styles.tagName}>{tag.name}</span>
                            </label>
                        ))}
                    </div>
                </div>
                {error && <p className={styles.error}>{error}</p>}
                <button type="submit" className={styles.submitButton}>
                    Create Profile
                </button>
            </form>
        </div>
    );
};

export default CreateProfile;